package ru.petr.songapp.screens.songScreen.song

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.petr.songapp.screens.songScreen.song.models.Song

interface SongComponent {
    val song: Value<Song>
    val fontSize: Value<Int>
}