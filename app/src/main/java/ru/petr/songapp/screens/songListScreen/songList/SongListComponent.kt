package ru.petr.songapp.screens.songListScreen.songList

import com.arkivanov.decompose.value.Value
import ru.petr.songapp.screens.common.fullTextSearch.FullSearchResult
import ru.petr.songapp.screens.common.searchBar.SearchBarComponent

interface SongListComponent {

    val songItems: Value<List<SongItem>>

    val searchIsActive: Value<Boolean>

    val fullTextSearchIsActive: Value<Boolean>

    val fullSearchResult: Value<FullSearchResult>

    fun onSongClicked(id: Int)

    fun onFullTextSearch()

    data class SongItem(
        val id: Int,
        val numInColl: Int,
        val name: String
    )
}