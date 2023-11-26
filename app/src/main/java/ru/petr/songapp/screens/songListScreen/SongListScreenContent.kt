package ru.petr.songapp.screens.songListScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.pages.Pages
import com.arkivanov.decompose.extensions.compose.jetbrains.pages.PagesScrollAnimation
import ru.petr.songapp.screens.common.searchBar.SearchBarContent
import ru.petr.songapp.screens.songListScreen.songList.SongListContent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongListScreenContent(component: SongListScreenComponent,
                          modifier: Modifier = Modifier) {
    Column (modifier) {
        Pages(
                modifier = Modifier.weight(1f),
                pages = component.collectionPages,
                onPageSelected = component::selectCollectionByIndex,
                scrollAnimation = PagesScrollAnimation.Default,
        ) { _, page ->
            SongListContent(component = page)
        }
        SearchBarContent(component.searchBarComponent)
    }
}