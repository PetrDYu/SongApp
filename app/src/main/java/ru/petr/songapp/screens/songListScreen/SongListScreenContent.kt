package ru.petr.songapp.screens.songListScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetbrains.pages.Pages
import com.arkivanov.decompose.extensions.compose.jetbrains.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import ru.petr.songapp.R
import ru.petr.songapp.screens.common.searchBar.SearchBarContent
import ru.petr.songapp.screens.songListScreen.songList.SongListContent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongListScreenContent(component: SongListScreenComponent,
                          modifier: Modifier = Modifier) {
    val selectedPage = component.collectionPages.subscribeAsState().value.selectedIndex
    Scaffold (
        modifier,
        topBar = { SongListScreenTopBar(collectionName = component.collections[selectedPage].name) },
    ) { paddingValues ->
        Column (Modifier.padding(paddingValues)) {
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
}

@Composable
fun SongListScreenTopBar(collectionName: String) {
    Box(Modifier
            .background(colorResource(id = R.color.main_white))
            .fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth(0.7f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            with(LocalDensity.current) {
                Icon(
                    modifier = Modifier
                        .clickable { }
                        .padding(vertical = 10.dp, horizontal = 20.dp)
                        .size(25.sp.toDp()),
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = colorResource(id = R.color.main_blue)
                )
            }
            Text(
                collectionName.uppercase(),
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(vertical = 10.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SongListScreenTopBarPreview() {
    SongListScreenTopBar("Будем петь и славить")
}