package ru.petr.songapp.screens.common.fullTextSearch

interface FullTextSearchComponent {

    val searchData: FullSearchData

    fun activateSearch(isActive: Boolean,
                       searchText: String = "")

    fun updateSearchResult(searchText: String)

    fun clearSearchResult()
}