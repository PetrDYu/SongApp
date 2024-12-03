package ru.petr.songapp.screens.common.fullTextSearch

interface FullTextSearchComponent {

    val searchData: FullSearchData

    fun activateSearch(isActive: Boolean,
                       searchText: String = "")

    fun updateSearchResult(searchTextWithoutSpecialSymbols: String)

    fun clearSearchResult(isActive: Boolean)
}