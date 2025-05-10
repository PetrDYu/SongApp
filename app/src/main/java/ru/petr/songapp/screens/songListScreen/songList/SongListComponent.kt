package ru.petr.songapp.screens.songListScreen.songList

import com.arkivanov.decompose.value.Value
import ru.petr.songapp.screens.common.fullTextSearch.FullSearchData
import ru.petr.songapp.screens.songListScreen.songList.scrollbar.ScrollbarComponent

interface SongListComponent {

    val songItems: Value<List<SongItem>>

    val searchIsActive: Value<Boolean>

    val fullTextSearchData: FullSearchData

    val scrollbar: ScrollbarComponent

    val isInGridMode: Value<Boolean>

    fun onSongClicked(id: Int)

    fun onFullTextSearch()

    data class SongItem(
        val id: Int,
        val numInColl: Int,
        val name: String
    )
}