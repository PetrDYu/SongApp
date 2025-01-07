package ru.petr.songapp.screens.songScreen.song

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.petr.songapp.screens.songScreen.song.models.Song
import ru.petr.songapp.screens.songScreen.song.models.SongShowTypes
import ru.petr.songapp.screens.songScreen.song.models.SongView

@Composable
fun SongContent(component: SongComponent,
                modifier: Modifier = Modifier,
                onChorusOffsetChanged: (Int, Int) -> Unit) {
    SongViewer(modifier = modifier,
               song = component.song.subscribeAsState().value,
               fontSize = component.fontSize.subscribeAsState().value,
               onChorusOffsetChanged = onChorusOffsetChanged)
}

@Composable
fun SongViewer(modifier: Modifier, song: Song?, fontSize: Int, onChorusOffsetChanged: (Int, Int) -> Unit) {
    Box(modifier = modifier
        .fillMaxSize()
    ) {
        if (song == null) {
            Text("Ошибка в процессе загрузки")
        } else {
            SongView(modifier = Modifier.padding(start = 10.dp,
                                                 end = 20.dp,
                                                 top = 30.dp,
                                                 bottom=200.dp),
                     showType = SongShowTypes.VIEW,
                     song = song,
                     fontSize = fontSize,
                     onChorusOffsetChanged = onChorusOffsetChanged)
        }
    }
}