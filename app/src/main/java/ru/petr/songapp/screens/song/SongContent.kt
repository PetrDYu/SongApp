package ru.petr.songapp.screens.song

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import ru.petr.songapp.screens.song.models.Song
import ru.petr.songapp.screens.song.models.SongShowTypes
import ru.petr.songapp.screens.song.models.SongView

@Composable
fun SongContent(component: SongComponent, modifier: Modifier = Modifier) {
    SongViewer(modifier = modifier,
               song = component.song.subscribeAsState().value,
               fontSize = 20)
}

@Composable
fun SongViewer(modifier: Modifier, song: Song?, fontSize: Int) {
    Box(modifier = modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        //        .horizontalScroll(rememberScrollState())
    ) {
        if (song == null) {
            Text("Ошибка в процессе загрузки")
        } else {
            SongView(modifier = Modifier.padding(start = 10.dp,
                                                 end = 20.dp,
                                                 top = 30.dp,
                                                 bottom=100.dp),
                     showType = SongShowTypes.VIEW,
                     song = song,
                     fontSize = fontSize)
        }
    }
}