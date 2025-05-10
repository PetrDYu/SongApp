package ru.petr.songapp.screens.common.searchBar

import androidx.core.text.isDigitsOnly
import com.arkivanov.decompose.value.Value
import ru.petr.songapp.database.room.songData.utils.removeSpecialSymbolsAndGetPositions
import ru.petr.songapp.screens.songListScreen.songList.SongListComponent

/**
 * Interface for the search bar component that provides search functionality.
 * Manages search text input and search activation state.
 */
interface SearchBarComponent {
    /**
     * Current text in the search field
     */
    val searchText: Value<String>

    /**
     * Flag indicating whether search is currently active
     */
    val searchIsActive: Value<Boolean>

    /**
     * Updates the search text with a new value
     * @param newText New text to set in the search field
     */
    fun onChangeSearchText(newText: String)

    /**
     * Initiates a search operation with the current search text
     */
    fun onSearch()

    companion object {
        /**
         * Filters song list based on search text.
         * - If search text is empty, returns the original list
         * - If search text contains only digits, filters by song number
         * - Otherwise filters by song name (case insensitive)
         * 
         * @param songList Original list of songs to be filtered
         * @param searchText Text to search for
         * @return Filtered list of songs matching the search criteria
         */
        fun updateSongList(songList: List<SongListComponent.SongItem>, searchText: String): List<SongListComponent.SongItem> {
            val updatedSongList: List<SongListComponent.SongItem> = if (searchText == "") {
                songList
            } else if (searchText.isDigitsOnly()) {
                if (searchText.length < 9) {
                    songList.filter { searchText.toInt() == it.numInColl }
                } else {
                    songList
                }
            } else {
                songList.filter { searchText.uppercase() in removeSpecialSymbolsAndGetPositions(it.name.uppercase()).first }
            }
            return updatedSongList
        }
    }
}