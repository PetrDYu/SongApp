package ru.petr.songapp.screens.songListScreen.songList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import ru.petr.songapp.R
import ru.petr.songapp.screens.common.fullTextSearch.FullSearchResult
import ru.petr.songapp.screens.common.fullTextSearch.FullSearchResultItem

@Composable
fun SongListContent(component: SongListComponent, modifier: Modifier = Modifier) {
    SongList(modifier,
             songs = component.songItems.subscribeAsState().value,
             onSongNameClick = component::onSongClicked,
             searchIsActive = component.searchIsActive.subscribeAsState().value,
             fullTextSearchIsActive = component.fullTextSearchIsActive.subscribeAsState().value,
             fullTextSearchResult = component.fullSearchResult.subscribeAsState().value) {
        component.onFullTextSearch()
    }
}


@Composable
fun SongList(modifier: Modifier = Modifier,
             songs: List<SongListComponent.SongItem>,
             onSongNameClick: (id:Int) -> Unit,
             searchIsActive: Boolean,
             fullTextSearchIsActive: Boolean,
             fullTextSearchResult: FullSearchResult,
             onFullTextSearchClick: () -> Unit,
){
    Box(modifier) {
        if (songs.isEmpty() && fullTextSearchResult.resultsList.isEmpty()) {
            if (searchIsActive) {
                Column {
                    Text(
                        stringResource(
                            id = R.string.not_found_songs_in_collection),
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
                    )
                    if (!fullTextSearchIsActive) {
                        FullSearchButton(onFullTextSearchClick)
                    }

                }
            } else {
                Text(
                    stringResource(
                            id = R.string.not_added_songs_in_collection),
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
                )
            }
        } else {
            LazyColumn(
                    Modifier
                        .fillMaxSize()
            ) {
                items(songs.size) { index ->
                    SongRow(songs[index], onSongNameClick)

                    if (index != songs.size - 1 || fullTextSearchIsActive) {
                        SongRowDivider()
                    }
                }

                if (searchIsActive) {
                    if (!fullTextSearchIsActive) {
                        items(1) {
                            FullSearchButton(onFullTextSearchClick)
                        }
                    } else {
                        items(fullTextSearchResult.resultsList.size) { index ->
                            fullTextSearchResult.resultsList[index].also {resultItem ->
                                SongRow(
                                    song = SongListComponent.SongItem(resultItem.song.id,
                                                                      resultItem.song.songData.numberInCollection,
                                                                      resultItem.song.songData.name),
                                    onSongNameClick = onSongNameClick,
                                    fullTextSearchIsActive = true,
                                    fullSearchResultItem = resultItem)
                            }
                            if (index != fullTextSearchResult.resultsList.size - 1) {
                                SongRowDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SongRow(song: SongListComponent.SongItem,
            onSongNameClick: (id:Int) -> Unit,
            fontSize: Int = 20,
            fullTextSearchIsActive: Boolean = false,
            fullSearchResultItem: FullSearchResultItem? = null
) {
    Column (Modifier
                .clickable { onSongNameClick(song.id) }
                .padding(vertical = 10.dp, horizontal = 20.dp)
                .fillMaxWidth()
    ) {
        Row {
            Text(
                "${song.numInColl}. ",
                fontSize = fontSize.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                song.name,
                fontSize = 20.sp
            )
        }
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
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun SongRowDivider(modifier: Modifier = Modifier) {
    Divider(modifier.padding(horizontal = 20.dp))
}

@Composable
fun FullSearchButton(onFullTextSearchClick: () -> Unit) {
    Button(
        modifier = Modifier
            .padding(horizontal = 40.dp, vertical = 10.dp)
            .fillMaxWidth()
            .height(40.dp),
        onClick = { onFullTextSearchClick() }
    ) {
        Text(stringResource(id = R.string.search_by_full_text))
    }
}