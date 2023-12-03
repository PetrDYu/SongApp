package ru.petr.songapp.screens.common.fullTextSearch

import ru.petr.songapp.database.room.songData.SongDBModel

class FullSearchResult (
    val searchText: String,
    val resultsList: List<FullSearchResultItem> = listOf()
)

data class FullSearchResultItem(val song: SongDBModel,
                                val prevWords: String,
                                val searchedText: String,
                                val nextWords: String)