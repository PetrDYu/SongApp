package ru.petr.songapp.screens.song

import com.arkivanov.decompose.value.MutableValue
import ru.petr.songapp.screens.song.models.Song

interface SongComponent {
    val song: MutableValue<Song>
}