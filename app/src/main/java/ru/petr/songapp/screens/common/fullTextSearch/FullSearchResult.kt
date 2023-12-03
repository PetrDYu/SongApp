package ru.petr.songapp.screens.common.fullTextSearch

import com.arkivanov.decompose.value.Value
import ru.petr.songapp.database.room.songData.SongDBModel

data class FullSearchResult (
    val searchText: String = "",
    val resultsList: List<FullSearchResultItem> = listOf()
)

data class FullSearchResultItem(val song: SongDBModel,
                                val prevWords: String,
                                val searchedText: String,
                                val nextWords: String)

data class FullSearchData(
    val result: Value<FullSearchResult>,
    val fullSearchIsActive: Value<Boolean>,
    val fullSearchIsInProgress: Value<Boolean>,
)