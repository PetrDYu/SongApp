package ru.petr.songapp.screens.common.fullTextSearch

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.petr.songapp.commonAndroid.database
import ru.petr.songapp.database.room.songData.SongDBModel

class DefaultFullTextSearchComponent(
    componentContext: ComponentContext,
    private val collectionId: Int
) : FullTextSearchComponent, ComponentContext by componentContext {
    private val _searchResult: MutableValue<FullSearchResult> = MutableValue(FullSearchResult("", listOf()))

    private val _searchIsActive = MutableValue(false)

    private val _searchIsInProgress = MutableValue(false)

    override val searchData: FullSearchData
        get() = FullSearchData(_searchResult, _searchIsActive, _searchIsInProgress)

    override fun activateSearch(isActive: Boolean,
                                searchText: String) {
        _searchIsActive.update { isActive }
        if (isActive) {
            updateSearchResult(searchText)
        } else {
            clearSearchResult()
        }
    }

    private val scope = CoroutineScope(Job())

    override fun updateSearchResult(searchText: String) {
        scope.launch {
            val results: MutableList<FullSearchResultItem> = mutableListOf()
            _searchIsInProgress.update { true }
            database
                .SongDao()
                .getCollectionSongs(collectionId)
                .first()
                .forEach {songDataForCollection ->
                    val song = database.SongDao().getSongById(songDataForCollection.id).first()
                    val foundIndex = song.plainText.indexOf(searchText, ignoreCase = true)
                    if (foundIndex != -1) {
                        val result = getFullSearchResultItem(song,
                                                             foundIndex,
                                                             searchText.length,
                                                             song.plainText)
                        results.add(result)
                    }
                }
            _searchResult.update { FullSearchResult(searchText, results) }
            _searchIsInProgress.update { false }
        }
    }

    override fun clearSearchResult() {
        _searchResult.update { FullSearchResult("", listOf()) }
        _searchIsActive.update { false }
        _searchIsInProgress.update { false }
    }

    private fun getFullSearchResultItem(song: SongDBModel,
                                        startIndex: Int,
                                        length: Int,
                                        text: String): FullSearchResultItem {
        var isLastWord = false
        var nextWords = ""
        var curNextIndex = startIndex + length + 1
        for (numWord in 0..2) {
            val nextSpaceIndex = text.indexOf(' ', startIndex = curNextIndex)
            if (nextSpaceIndex != -1) {
                nextWords += " " + text.substring(curNextIndex..<nextSpaceIndex)
                curNextIndex = nextSpaceIndex + 1
            } else {
                isLastWord = true
                break
            }
        }
        if (!isLastWord) {
            nextWords += "..."
        }

        isLastWord = false
        var prevWords = ""
        var curPrevIndex = text.length - startIndex + 1
        val reversedText = text.reversed()
        for (numWord in 0..2) {
            val prevSpaceIndex = reversedText.indexOf(' ', startIndex = curPrevIndex)
            if (prevSpaceIndex != -1) {
                prevWords = reversedText.substring(curPrevIndex..<prevSpaceIndex).reversed() + " " + prevWords
                curPrevIndex = prevSpaceIndex + 1
            } else {
                isLastWord = true
                break
            }
        }
        if (!isLastWord) {
            prevWords = "...$prevWords"
        }

        return FullSearchResultItem(song,
                                    prevWords,
                                    text.substring(startIndex, startIndex + length),
                                    nextWords)
    }
}