package ru.petr.songapp.screens.songListScreen.songList

import com.arkivanov.decompose.value.Value
import ru.petr.songapp.screens.common.fullTextSearch.FullSearchData
import ru.petr.songapp.screens.songListScreen.songList.scrollbar.ScrollbarComponent

/**
 * Interface for the song list component that manages the display of songs in a list or grid.
 * Provides song items, search functionality, and handles song selection.
 */
interface SongListComponent {

    /**
     * List of song items to be displayed
     */
    val songItems: Value<List<SongItem>>

    /**
     * Flag indicating whether search is currently active
     */
    val searchIsActive: Value<Boolean>

    /**
     * Data for full-text search functionality
     */
    val fullTextSearchData: FullSearchData

    /**
     * Component for scrollbar functionality
     */
    val scrollbar: ScrollbarComponent

    /**
     * Flag indicating whether songs are displayed in grid mode
     */
    val isInGridMode: Value<Boolean>

    /**
     * Handles song selection by ID
     * @param id ID of the selected song
     */
    fun onSongClicked(id: Int)

    /**
     * Initiates a full-text search operation
     */
    fun onFullTextSearch()

    /**
     * Data class representing a song item in the list view.
     * Contains basic information for displaying a song in the list.
     *
     * @property id Unique identifier for the song
     * @property numInColl Song number within its collection
     * @property name Title of the song
     */
    data class SongItem(
        val id: Int,
        val numInColl: Int,
        val name: String
    )
}