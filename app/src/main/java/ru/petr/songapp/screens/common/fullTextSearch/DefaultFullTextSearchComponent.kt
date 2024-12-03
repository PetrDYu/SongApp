package ru.petr.songapp.screens.common.fullTextSearch

import androidx.core.text.isDigitsOnly
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.petr.songapp.commonAndroid.databaseComponent
import ru.petr.songapp.database.room.songData.SongDBModel
import ru.petr.songapp.database.room.songData.utils.getIndexTuning
import ru.petr.songapp.database.room.songData.utils.getLenTuning
import ru.petr.songapp.screens.songListScreen.songList.SongListComponent

class DefaultFullTextSearchComponent(
    componentContext: ComponentContext,
    private val collectionId: Int,
    private val currentSongs: Value<List<SongListComponent.SongItem>>
) : FullTextSearchComponent, ComponentContext by componentContext {
    private val _searchResult: MutableValue<FullSearchResult> = MutableValue(FullSearchResult("", listOf()))

    private val _searchIsActive = MutableValue(false)

    private val _searchIsInProgress = MutableValue(false)

    private var _searchText = ""

    private val scope = CoroutineScope(Job())
    private var curJob: Job? = null

    override val searchData: FullSearchData
        get() = FullSearchData(_searchResult, _searchIsActive, _searchIsInProgress)

    init {
        currentSongs.subscribe {
            if (_searchIsActive.value && _searchText != "") {
                updateSearchResult(_searchText)
            }
        }
    }

    override fun activateSearch(isActive: Boolean,
                                searchText: String) {
        _searchIsActive.update { isActive }
        if (isActive) {
            updateSearchResult(searchText)
        } else {
            clearSearchResult(false)
        }
    }

    override fun updateSearchResult(searchTextWithoutSpecialSymbols: String) {
        curJob?.cancel()
        if (!searchTextWithoutSpecialSymbols.isDigitsOnly()) {
            curJob = scope.launch {
                val results: MutableList<FullSearchResultItem> = mutableListOf()
                _searchIsInProgress.update { true }
                val currentSongsIds = currentSongs.value.map { it.id }
                databaseComponent.getAllSongsInCollection(collectionId).value
                    .forEach { songDataForCollection ->
                        if (!currentSongsIds.contains(songDataForCollection.id)) {
                            val song = databaseComponent.getSongById(songDataForCollection.id)
                            val foundIndex =
                                song.plainTextWithoutSpecialSymbols.indexOf(
                                    searchTextWithoutSpecialSymbols,
                                    ignoreCase = true)
                            if (foundIndex != -1) {
                                val startIndex =
                                    foundIndex + getIndexTuning(
                                        foundIndex,
                                        song.specialSymbolPositions)
                                val length = searchTextWithoutSpecialSymbols.length +
                                    getLenTuning(
                                        foundIndex,
                                        searchTextWithoutSpecialSymbols.length,
                                        song.specialSymbolPositions)
                                val result = getFullSearchResultItem(
                                    song,
                                    startIndex,
                                    length,
                                    song.plainText)
                                results.add(result)
                            }
                        }
                    }
                _searchResult.update { FullSearchResult(searchTextWithoutSpecialSymbols, results) }
                _searchIsInProgress.update { false }
            }
        } else {
            clearSearchResult(true)
            _searchIsActive.update { true }
        }
    }

    override fun clearSearchResult(isActive: Boolean) {
        _searchResult.update { FullSearchResult("", listOf()) }
        _searchIsActive.update { isActive }
        _searchIsInProgress.update { false }
    }

    private fun getFullSearchResultItem(song: SongDBModel,
                                        startIndex: Int,
                                        length: Int,
                                        text: String): FullSearchResultItem {
        var isLastWord = false
        var nextWords = ""
        var curNextIndex = startIndex + length
        if (text[curNextIndex] != ' ') {
            curNextIndex = text.indexOf(' ', startIndex = curNextIndex)
        }
        val newEndIndex = curNextIndex
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
        var curPrevIndex = text.length - startIndex
        val reversedText = text.reversed()
        if (reversedText[curPrevIndex] != ' ') {
            curPrevIndex = reversedText.indexOf(' ', startIndex = curPrevIndex)
        }
        val newStartIndex = reversedText.length - curPrevIndex
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
                                    text.substring(newStartIndex, newEndIndex),
                                    nextWords)
    }
}