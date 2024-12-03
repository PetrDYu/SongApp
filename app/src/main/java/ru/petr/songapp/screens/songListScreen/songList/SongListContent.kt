package ru.petr.songapp.screens.songListScreen.songList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.petr.songapp.R
import ru.petr.songapp.screens.common.fullTextSearch.FullSearchData
import ru.petr.songapp.screens.common.fullTextSearch.FullSearchResultItem

@Composable
fun SongListContent(component: SongListComponent, modifier: Modifier = Modifier) {
    SongList(modifier.background(brush =
                                 Brush.verticalGradient(
                                     colors = listOf(
                                         MaterialTheme.colorScheme.primary,
                                         MaterialTheme.colorScheme.secondary
                                     )
                                 )),
         songs = component.songItems.subscribeAsState().value,
         onSongNameClick = component::onSongClicked,
         searchIsActive = component.searchIsActive.subscribeAsState().value,
         fullTextSearchData = component.fullTextSearchData,
    ) {
        component.onFullTextSearch()
    }
}


@Composable
fun SongList(modifier: Modifier = Modifier,
             songs: List<SongListComponent.SongItem>,
             onSongNameClick: (id:Int) -> Unit,
             searchIsActive: Boolean,
             fullTextSearchData: FullSearchData,
             onFullTextSearchClick: () -> Unit,
){
    val fullTextSearchResult by fullTextSearchData.result.subscribeAsState()
    val fullTextSearchIsActive by fullTextSearchData.fullSearchIsActive.subscribeAsState()
    val fullTextSearchIsInProgress by fullTextSearchData.fullSearchIsInProgress.subscribeAsState()

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
                MessageCard(stringResource( id = R.string.not_added_songs_in_collection))
            }
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(songs.size) { index ->
                    SongCard(songs[index], onSongNameClick)
                }

                if (searchIsActive) {
                    if (!fullTextSearchIsActive) {
                        items(1) {
                            FullSearchButton(onFullTextSearchClick)
                        }
                    }  else if (fullTextSearchIsInProgress) {
                        items(1) {
                            FullSearchProgressBar()
                        }
                    } else {
                        items(fullTextSearchResult.resultsList.size) { index ->
                            fullTextSearchResult.resultsList[index].also {resultItem ->
                                SongCard(
                                    song = SongListComponent.SongItem(resultItem.song.id,
                                                                      resultItem.song.songData.numberInCollection,
                                                                      resultItem.song.songData.name),
                                    onSongNameClick = onSongNameClick,
                                    fullTextSearchIsActive = true,
                                    fullSearchResultItem = resultItem)
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
             fontSize: Int = 20,
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
                    fontSize = 20.sp
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
                        color = MaterialTheme.colorScheme.onPrimary
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