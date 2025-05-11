package ru.petr.songapp.screens.common.fullTextSearch

/**
 * Interface for a component that provides full text search functionality for songs.
 * This component manages search state and performs search operations on song lyrics.
 */
interface FullTextSearchComponent {

    /**
     * Contains all data related to the search state and results
     */
    val searchData: FullSearchData

    /**
     * Activates or deactivates the full text search
     *
     * @param isActive Whether to activate or deactivate the search
     * @param searchText Text to search for (default empty string)
     */
    fun activateSearch(isActive: Boolean,
                       searchText: String = "")

    /**
     * Updates search results based on the provided search text
     *
     * @param searchTextWithoutSpecialSymbols Clean search text without special symbols
     */
    fun updateSearchResult(searchTextWithoutSpecialSymbols: String)

    /**
     * Clears current search results
     *
     * @param isActive Whether the search should remain active after clearing
     */
    fun clearSearchResult(isActive: Boolean)
}