package ru.petr.songapp.screens.songListScreen.songList

import android.graphics.fonts.FontStyle
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import ru.petr.songapp.R
import ru.petr.songapp.screens.common.searchBar.SearchBarContent

@Composable
fun SongListContent(component: SongListComponent, modifier: Modifier = Modifier) {
    SongList(modifier,
             songs = component.songItems.subscribeAsState().value,
             onSongNameClick = component::onSongClicked,
             searchIsActive = component.searchIsActive.subscribeAsState().value,
             fullTextSearchIsActive = false) {

    }
}


@Composable
fun SongList(modifier: Modifier = Modifier,
             songs: List<SongListComponent.SongItem>,
             onSongNameClick: (id:Int) -> Unit,
             searchIsActive: Boolean,
             fullTextSearchIsActive: Boolean,
             //fullTextSearchResult: List<FullTextSearchResultItem>,
             onFullTextSearchClick: () -> Unit,
){
    Box(modifier) {
        if (songs.isEmpty()) {
            if (searchIsActive) {
                Text(
                        stringResource(
                                id = R.string.not_found_songs_in_collection),
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
                )
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
                    Row(
                            Modifier
                                .clickable { onSongNameClick(songs[index].id) }
                                .padding(vertical = 10.dp, horizontal = 20.dp)
                                .fillMaxWidth()

                    ) {
                        Text(
                                "${songs[index].numInColl}. ",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                //fontStyle = FontStyle.Italic
                        )
                        Text(
                                songs[index].name,
                                fontSize = 20.sp
                        )
                    }

                    if (index != songs.size - 1 || fullTextSearchIsActive)
                        Divider(Modifier.padding(horizontal = 20.dp))
                }

                if (searchIsActive) {
                    if (!fullTextSearchIsActive) {
                        items(1) {
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
                    } else {
                        //                    items(fullTextSearchResult.size) { index ->
                        //                        Text(
                        //                                "${fullTextSearchResult[index].song.numberInCollection}. ${fullTextSearchResult[index].song.name}",
                        //                                Modifier
                        //                                    .clickable { onSongNameClick(fullTextSearchResult[index].song.id) }
                        //                                    .padding(vertical = 10.dp)
                        //                                    .fillMaxWidth(),
                        //                                fontSize = 20.sp
                        //                        )
                        //                        if (index != fullTextSearchResult.size - 1)
                        //                            Divider()
                        //                    }
                    }
                }
            }
        }
    }
}