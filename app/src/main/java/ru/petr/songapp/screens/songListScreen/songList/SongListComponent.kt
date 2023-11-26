package ru.petr.songapp.screens.songListScreen.songList

import com.arkivanov.decompose.value.Value
import ru.petr.songapp.screens.common.searchBar.SearchBarComponent

interface SongListComponent {

    val songItems: Value<List<SongItem>>

    val searchIsActive: Value<Boolean>

    fun onSongClicked(id: Int)

    data class SongItem(
        val id: Int,
        val numInColl: Int,
        val name: String
    )
}