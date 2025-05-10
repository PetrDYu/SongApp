package ru.petr.songapp.screens.songListScreen.songList

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.petr.songapp.R
import ru.petr.songapp.screens.common.fullTextSearch.FullSearchData
import ru.petr.songapp.screens.common.fullTextSearch.FullSearchResultItem
import ru.petr.songapp.screens.songListScreen.songList.scrollbar.ScrollbarContent

val SONG_TEXT_SIZE = 20.sp

@Composable
fun SongListContent(component: SongListComponent, modifier: Modifier = Modifier) {
    val items by component.songItems.subscribeAsState()
    val searchIsActive by component.searchIsActive.subscribeAsState()
    val isInGridMode by component.isInGridMode.subscribeAsState()
    
    // Создаем состояние для списка
    val listState = rememberLazyListState()
    // Создаем scope для прокрутки списка
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(listState) {
        // Отслеживаем изменение размера первого видимого элемента списка
        launch {
            snapshotFlow {
                listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0
            }
                .distinctUntilChanged()  // реагировать только на изменения размера
                .collect { newHeight ->
                    component.scrollbar.setItemHeight(newHeight.toFloat())
                }
        }
    }

    val listScrollIsEnabled by component.scrollbar.listScrollIsEnabled.subscribeAsState()
    val targetListIndex by component.scrollbar.targetListIndex.subscribeAsState()
    val targetListOffset by component.scrollbar.targetListOffset.subscribeAsState()
    LaunchedEffect(listScrollIsEnabled, targetListIndex, targetListOffset) {
        if (listScrollIsEnabled) {
            scope.launch {
                listState.scrollToItem(index = targetListIndex, scrollOffset = targetListOffset)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Определим отображаемый режим на верхнем уровне
        val showGridView = !searchIsActive && isInGridMode
        
        AnimatedContent(
            targetState = showGridView,
            label = "ViewModeToggle",
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { isGridView ->
            if (isGridView) {
                // Режим сетки (только когда поиск не активен и выбран grid режим)
                SongNumberGrid(items, component::onSongClicked)
            } else {
                // Обычный список или режим поиска
                SongList(
                    Modifier.background(brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )),
                    songs = items,
                    onSongNameClick = component::onSongClicked,
                    searchIsActive = searchIsActive,
                    fullTextSearchData = component.fullTextSearchData,
                    onFullTextSearchClick = {
                        component.onFullTextSearch()
                    },
                    onDragScroll = component.scrollbar::updateListScrollOffset,
                    listState = listState
                )
            }
        }
        ScrollbarContent(
            component = component.scrollbar,
            modifier = Modifier.align(Alignment.TopEnd),
        )
    }
}

@Composable
fun SongNumberGrid(items: List<SongListComponent.SongItem>, onSongClicked: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive((SONG_TEXT_SIZE.value.toInt() * 2.5).dp),
            contentPadding = PaddingValues(5.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable { onSongClicked(item.id) },
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = item.numInColl.toString(),
                            fontSize = SONG_TEXT_SIZE,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SongList(modifier: Modifier = Modifier,
             songs: List<SongListComponent.SongItem>,
             onSongNameClick: (id:Int) -> Unit,
             searchIsActive: Boolean,
             fullTextSearchData: FullSearchData,
             onFullTextSearchClick: () -> Unit,
             onDragScroll: (Int, Int, Boolean) -> Unit,
             listState: LazyListState
){
    val fullTextSearchResult by fullTextSearchData.result.subscribeAsState()
    val fullTextSearchIsActive by fullTextSearchData.fullSearchIsActive.subscribeAsState()
    val fullTextSearchIsInProgress by fullTextSearchData.fullSearchIsInProgress.subscribeAsState()

    // Эффект для обновления смещения scrollbar при прокрутке списка
    LaunchedEffect(listState) {
        snapshotFlow {
            // Берем индекс и смещение первого видимого элемента
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }
            .distinctUntilChanged()
            .collect { (index, offset) ->
                onDragScroll(index, offset, false)
            }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.isScrollInProgress
        }
            .distinctUntilChanged()
            .collect {
                if (!it) {
                    delay(100)
                    if (!listState.isScrollInProgress) {
                        onDragScroll(
                            listState.firstVisibleItemIndex,
                            listState.firstVisibleItemScrollOffset,
                            true
                        )
                    }
                }
            }
    }

    Box(modifier.fillMaxSize()) {
        if (songs.isEmpty() && fullTextSearchResult.resultsList.isEmpty()) {
            if (searchIsActive) {
                if (fullTextSearchIsInProgress) {
                    Column {
                        MessageCard(message = stringResource(id = R.string.not_fount_songs_by_name))
                        FullSearchProgressBar()
                    }
                } else {
                    MessageCard(stringResource(id = R.string.not_found_songs_in_collection))
                }
            } else {
                MessageCard(stringResource(id = R.string.not_added_songs_in_collection))
            }
        } else {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                items(songs.size) { index ->
                    SongCard(songs[index], onSongNameClick)
                }
                
                if (searchIsActive) {
                    if (!fullTextSearchIsActive) {
                        items(1) {
                            FullSearchButton(onFullTextSearchClick)
                        }
                    } else if (fullTextSearchIsInProgress) {
                        items(1) {
                            FullSearchProgressBar()
                        }
                    } else {
                        items(fullTextSearchResult.resultsList.size) { index ->
                            fullTextSearchResult.resultsList[index].also { resultItem ->
                                SongCard(
                                    song = SongListComponent.SongItem(
                                        resultItem.song.id,
                                        resultItem.song.songData.numberInCollection,
                                        resultItem.song.songData.name
                                    ),
                                    onSongNameClick = onSongNameClick,
                                    fullTextSearchIsActive = true,
                                    fullSearchResultItem = resultItem
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageCard(message: String) {
    Card (Modifier
              .fillMaxWidth()
              .padding(vertical = 5.dp, horizontal = 20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Text(message, modifier = Modifier.padding(10.dp))
    }
}

@Composable
fun SongCard(song: SongListComponent.SongItem,
             onSongNameClick: (id:Int) -> Unit,
             fontSize: Int = SONG_TEXT_SIZE.value.toInt(),
             fullTextSearchIsActive: Boolean = false,
             fullSearchResultItem: FullSearchResultItem? = null
) {
    Card (
        onClick = { onSongNameClick(song.id) },
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "\n${song.numInColl}. \n",
                fontSize = fontSize.sp
            )
            Column {
                Text(
                    song.name,
                    fontSize = fontSize.sp
                )
                
                if (fullTextSearchIsActive && fullSearchResultItem != null) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                                append(fullSearchResultItem.prevWords)
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(fullSearchResultItem.searchedText)
                            }
                            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                                append(fullSearchResultItem.nextWords)
                            }
                        },
                        fontSize = (fontSize - 5).sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun FullSearchButton(onFullTextSearchClick: () -> Unit) {
    Button(
        modifier = Modifier
            .padding(horizontal = 40.dp, vertical = 10.dp)
            .fillMaxWidth()
            .height(40.dp),
        onClick = { onFullTextSearchClick() },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(stringResource(id = R.string.search_by_full_text))
    }
}

@Composable
fun FullSearchProgressBar(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center) {
        CircularProgressIndicator(Modifier.padding(top = 20.dp, bottom = 70.dp),
                                  MaterialTheme.colorScheme.primary)
    }
}

@Preview(showBackground = true)
@Composable
fun SongListContentPreview() {
    
}