package ru.petr.songapp.screens.songScreen.song

import com.arkivanov.decompose.value.Value
import ru.petr.songapp.screens.songScreen.song.models.Song

interface SongComponent {
    val songId: Int

    val song: Value<Song>
    val fontSize: Value<Int>

    val name: Value<String>
    val numberInCollection: Value<Int>
    val isFavorite: Value<Boolean>
}