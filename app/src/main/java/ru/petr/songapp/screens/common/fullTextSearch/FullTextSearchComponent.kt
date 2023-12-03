package ru.petr.songapp.screens.common.fullTextSearch

import com.arkivanov.decompose.value.Value

interface FullTextSearchComponent {

    val searchResult: Value<FullSearchResult>

    fun updateSearchResult(searchText: String)

    fun clearSearchResult()
}