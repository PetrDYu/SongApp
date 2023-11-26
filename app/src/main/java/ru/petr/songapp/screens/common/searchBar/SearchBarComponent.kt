package ru.petr.songapp.screens.common.searchBar

import androidx.core.text.isDigitsOnly
import com.arkivanov.decompose.value.Value
import ru.petr.songapp.screens.songListScreen.songList.SongListComponent

interface SearchBarComponent {
    val searchText: Value<String>

    val searchIsActive: Value<Boolean>

    fun onChangeSearchText(newText: String)

    fun onSearch()

    companion object {
        fun updateSongList(songList: List<SongListComponent.SongItem>, searchText: String): List<SongListComponent.SongItem> {
            val updatedSongList: List<SongListComponent.SongItem> = if (searchText == "") {
                songList
            } else if (searchText.isDigitsOnly()) {
                songList.filter { searchText.toInt() == it.numInColl }
            } else {
                songList.filter { searchText.uppercase() in it.name.uppercase() }
            }
            return updatedSongList
        }
    }
}