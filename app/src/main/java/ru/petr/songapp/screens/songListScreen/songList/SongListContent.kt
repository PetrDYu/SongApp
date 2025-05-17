package ru.petr.songapp.screens.songListScreen.songList

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalDensity
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

/**
 * Default text size for song titles and numbers
 */
val SONG_TEXT_SIZE = 20.sp

/**
 * Main composable for displaying the song list content.
 * Handles different view modes (grid/list) and search results display.
 *
 * @param component The SongListComponent that manages the song list data and logic
 * @param modifier Optional modifier for customizing the layout
 */

@Composable
fun SongListContent(component: SongListComponent, modifier: Modifier = Modifier) {
    val items by component.songItems.subscribeAsState()
    val searchIsActive by component.searchIsActive.subscribeAsState()
    val isInGridMode by component.isInGridMode.subscribeAsState()

    val listState = rememberLazyListState()
    // Scope for list scrolling
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(listState) {
        // Track the resizing of the first visible element of the list
        launch {
            snapshotFlow {
                listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0
            }
                .distinctUntilChanged()  // respond only to changes in size
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

    Box(
        modifier = modifier
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
        // Let's define the displayed mode on the top level
        val showGridView = !searchIsActive && isInGridMode
        
        AnimatedContent(
            targetState = showGridView,
            label = "ViewModeToggle",
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { isGridView ->
            if (isGridView) {
                // Grid mode (only when search is not active and grid mode is selected)
                SongNumberGrid(items = items, onSongClicked = component::onSongClicked)
            } else {
                // Normal list or search mode
                SongList(
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

/**
 * Composable for displaying songs in a grid layout with just song numbers.
 * Used for quick navigation through large song collections.
 *
 * @param modifier Modifier for customizing the layout
 * @param items List of songs to display in the grid
 * @param onSongClicked Callback when a song is selected from the grid
 */
@Composable
fun SongNumberGrid(modifier: Modifier = Modifier, 
                   items: List<SongListComponent.SongItem>, 
                   onSongClicked: (Int) -> Unit) {
    val density = LocalDensity.current
    LazyVerticalGrid(
        columns = GridCells.Adaptive(with(density){(SONG_TEXT_SIZE.value.toInt() * 3.5).sp.toDp()}),
        contentPadding = PaddingValues(5.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(items) { item ->
            Card(
                modifier = Modifier
                    .aspectRatio(1f),
                onClick = { onSongClicked(item.id) },
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

/**
 * Composable for displaying songs in a list layout with detailed information.
 * Also displays search results and full-text search functionality.
 * 
 * @param modifier Modifier for customizing the layout
 * @param songs List of songs to display
 * @param onSongNameClick Callback when a song is selected
 * @param searchIsActive Whether search is currently active
 * @param fullTextSearchData Data for full text search functionality
 * @param onFullTextSearchClick Callback to initiate full text search
 * @param onDragScroll Callback to update scrollbar position during scrolling
 * @param listState State object for the lazy list to manage scrolling
 */
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

    // Effect to update the scrollbar offset when scrolling the list
    LaunchedEffect(listState) {
        snapshotFlow {
            // Take the index and offset of the first visible element
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }
            .distinctUntilChanged()
            .collect { (index, offset) ->
                onDragScroll(index, offset, false)
            }
    }

    // Effect to update the scrollbar offset when scrolling stops
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

/**
 * Card composable for displaying a message when no songs are available or found.
 * 
 * @param message Text message to display in the card
 */
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

/**
 * Card composable for displaying a song with its number and title.
 * Also shows search result highlights when applicable.
 *
 * @param song Song item to display
 * @param onSongNameClick Callback when the song is selected
 * @param fontSize Font size for the song text
 * @param fullTextSearchIsActive Whether full text search is active
 * @param fullSearchResultItem Optional search result data with highlighted text
 */
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
                "\n${song.numInColl}. ",
                fontSize = fontSize.sp,
                minLines = 3,
                maxLines = 3
            )
            Column {
                Text(
                    song.name,
                    fontSize = fontSize.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
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
                    )
                }
            }
        }
    }
}

/**
 * Button composable that initiates full text search.
 *
 * @param onFullTextSearchClick Callback when the button is clicked
 */
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

/**
 * Progress indicator composable shown during full text search operations.
 * 
 * @param modifier Optional modifier for customizing the layout
 */
@Composable
fun FullSearchProgressBar(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center) {
        CircularProgressIndicator(Modifier.padding(top = 20.dp, bottom = 70.dp),
                                  MaterialTheme.colorScheme.primary)
    }
}

/**
 * Preview composable for SongListContent
 */
@Preview(showBackground = true)
@Composable
fun SongListContentPreview() {
    
}