package ru.petr.songapp.screens.common.fullTextSearch

import com.arkivanov.decompose.value.Value
import ru.petr.songapp.database.room.songData.SongDBModel

/**
 * Data class representing the results of a full text search operation.
 * Contains the search text and a list of matching results.
 *
 * @property searchText The text that was searched for
 * @property resultsList List of search result items that match the search text
 */
data class FullSearchResult (
    val searchText: String = "",
    val resultsList: List<FullSearchResultItem> = listOf()
)

/**
 * Data class representing a single search result hit.
 * Includes the song that matched the search and context around the matched text.
 *
 * @property song The song database model that matched the search
 * @property prevWords Text that appears before the matched text (context)
 * @property searchedText The actual text that matched the search query
 * @property nextWords Text that appears after the matched text (context)
 */
data class FullSearchResultItem(val song: SongDBModel,
                                val prevWords: String,
                                val searchedText: String,
                                val nextWords: String)

/**
 * Data class that encapsulates all data related to full text search functionality.
 * Provides observable values for search results and search state.
 *
 * @property result Observable value containing the current search results
 * @property fullSearchIsActive Observable value indicating whether full search is active
 * @property fullSearchIsInProgress Observable value indicating whether a search operation is currently running
 */
data class FullSearchData(
    val result: Value<FullSearchResult>,
    val fullSearchIsActive: Value<Boolean>,
    val fullSearchIsInProgress: Value<Boolean>,
)