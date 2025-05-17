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
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Default implementation of the FullTextSearchComponent interface.
 * Performs full text search across song lyrics and manages search state.
 *
 * @param componentContext The Decompose component context
 * @param collectionId ID of the song collection to search within
 * @param currentSongs Value containing the current list of songs being displayed
 */
class DefaultFullTextSearchComponent(
    componentContext: ComponentContext,
    private val collectionId: Int,
    private val currentSongs: Value<List<SongListComponent.SongItem>>
) : FullTextSearchComponent, ComponentContext by componentContext {
    /**
     * Mutable container for search results
     */
    private val _searchResult: MutableValue<FullSearchResult> = MutableValue(FullSearchResult("", listOf()))

    /**
     * Mutable container for search active state
     */
    private val _searchIsActive = MutableValue(false)

    /**
     * Mutable container for search progress state
     */
    private val _searchIsInProgress = MutableValue(false)

    /**
     * Cached search text for reusing in refreshes
     */
    private var _searchText = ""

    /**
     * Coroutine scope for running search operations with lifecycle awareness
     */
    private val scope = CoroutineScope(EmptyCoroutineContext + Job())
    
    /**
     * Reference to the current search job for cancellation
     */
    private var curJob: Job? = null

    /**
     * Provides read-only access to search data
     */
    override val searchData: FullSearchData
        get() = FullSearchData(_searchResult, _searchIsActive, _searchIsInProgress)

    init {
        // Subscribe to song list changes to refresh search results when needed
        currentSongs.subscribe {
            if (_searchIsActive.value && _searchText != "") {
                updateSearchResult(_searchText)
            }
        }

        lifecycle.doOnDestroy {
            curJob?.cancel()
            scope.coroutineContext[Job]?.cancel()
        }
    }

    /**
     * Activates or deactivates the full text search mode.
     *
     * When activating, initiates a search with the provided text.
     * When deactivating, clears the search results while preserving the active state.
     *
     * @param isActive Whether the search mode is active
     * @param searchText The text to search for when activating
     */
    override fun activateSearch(isActive: Boolean, searchText: String) {
        _searchIsActive.update { isActive }
        if (isActive) {
            // If activating, update search with the provided text
            updateSearchResult(searchText)
        } else {
            // If deactivating, clear results
            clearSearchResult(false)
        }
    }

    /**
     * Updates search results based on the provided text.
     *
     * Cancels any ongoing search operation and initiates a new one if the search text contains non-digit characters.
     * For numeric search terms, clears the search results while keeping the search active.
     * The search is performed asynchronously in a coroutine.
     *
     * @param searchTextWithoutSpecialSymbols The search text with special symbols removed
     */
    override fun updateSearchResult(searchTextWithoutSpecialSymbols: String) {
        // Store search text for potential reuse
        _searchText = searchTextWithoutSpecialSymbols
        
        // Cancel any ongoing search operation
        curJob?.cancel()
        
        // Skip searching for numbers to prevent unnecessary operations from being performed
        if (!searchTextWithoutSpecialSymbols.isDigitsOnly()) {
            curJob = scope.launch {
                val results: MutableList<FullSearchResultItem> = mutableListOf()
                
                // Set search as in progress
                _searchIsInProgress.update { true }
                
                // Get IDs of songs that are already visible (matched by name)
                val currentSongsIds = currentSongs.value.map { it.id }
                
                // Search through all songs in the collection
                databaseComponent.getAllSongsInCollection(collectionId).value
                    .forEach { songDataForCollection ->
                        // Skip songs that are already in the current list (matched by name)
                        if (!currentSongsIds.contains(songDataForCollection.id)) {
                            // Get full song data to search through lyrics
                            val song = databaseComponent.getSongById(songDataForCollection.id)
                            // Perform case-insensitive substring search in the plain text without special symbols
                            val foundIndex = song.plainTextWithoutSpecialSymbols.indexOf(
                                searchTextWithoutSpecialSymbols,
                                ignoreCase = true
                            )

                            if (foundIndex != -1) {
                                // Found a match - calculate the actual position in the original text
                                // by accounting for special symbols that were removed
                                val startIndex = foundIndex + getIndexTuning(
                                    foundIndex,
                                    song.specialSymbolPositions
                                )
                                // Calculate the actual length of the match in the original text
                                // accounting for special symbols that were removed
                                val length = searchTextWithoutSpecialSymbols.length +
                                    getLenTuning(
                                        foundIndex,
                                        searchTextWithoutSpecialSymbols.length,
                                        song.specialSymbolPositions
                                    )

                                // Generate a search result item with context around the match
                                val result = getFullSearchResultItem(
                                    song,
                                    startIndex,
                                    length,
                                    song.plainText
                                )
                                // Add to results list
                                results.add(result)
                            }
                        }
                    }
                
                // Update search results
                _searchResult.update { FullSearchResult(searchTextWithoutSpecialSymbols, results) }
                
                // Mark search as no longer in progress
                _searchIsInProgress.update { false }
            }
        } else {
            // Clear search results if searching for digits only
            clearSearchResult(true)
            _searchIsActive.update { true }
        }
    }

    /**
     * Clears the current search results and updates the search state.
     *
     * This method resets the search result container to an empty state, updates the search active state
     * based on the provided parameter, and ensures the search is no longer marked as in progress.
     *
     * @param isActive Whether the search mode should remain active after clearing the results.
     *                 Set to false when deactivating the search, true to keep it active with empty results.
     */
    override fun clearSearchResult(isActive: Boolean) {
        _searchResult.update { FullSearchResult("", listOf()) }
        _searchIsActive.update { isActive }
        _searchIsInProgress.update { false }
    }

    /**
     * Extracts context around a matched text within a song and formats it for display.
     * 
     * The method performs the following operations:
     * 1. Identifies the exact matched text within the song's plain text
     * 2. Extracts up to 3 words before the match for context (if available)
     * 3. Extracts up to 3 words after the match for context (if available)
     * 4. Handles edge cases (start/end of text) gracefully
     * 5. Adds ellipsis (...) when context is truncated
     * 
     * The matching algorithm:
     * - For the matched text: Uses exact position and length from parameters
     * - For preceding context: Works with reversed text to efficiently find word boundaries
     * - For following context: Scans forward from match end to find word boundaries
     * 
     * @param song The song database model containing the matched text
     * @param startIndex The starting character index of the match in the plain text
     * @param length The length of the matched text segment
     * @param text The complete plain text of the song to search within
     * @return FullSearchResultItem containing:
     *         - The song model
     *         - Preceding context (up to 3 words)
     *         - The exact matched text
     *         - Following context (up to 3 words)
     * @throws IndexOutOfBoundsException if startIndex or length are invalid for the given text
     */
    private fun getFullSearchResultItem(song: SongDBModel,
                                        startIndex: Int,
                                        length: Int,
                                        text: String): FullSearchResultItem {
        // --- Extract text following the match ---
        var isLastWord = false
        var nextWords = ""
        var curNextIndex = startIndex + length
        
        // If match doesn't end at a space, find the end of the current word
        if (text[curNextIndex] != ' ') {
            curNextIndex = text.indexOf(' ', startIndex = curNextIndex)
        }
        
        // Store the end of the matched text/word
        val newEndIndex = curNextIndex
        curNextIndex++
        
        // Extract up to 3 words following the match
        for (numWord in 0..2) {
            val nextSpaceIndex = text.indexOf(' ', startIndex = curNextIndex)
            if (nextSpaceIndex != -1) {
                // Add word to context
                nextWords += " " + text.substring(curNextIndex..<nextSpaceIndex)
                curNextIndex = nextSpaceIndex + 1
            } else {
                // We've reached the end of the text
                isLastWord = true
                break
            }
        }
        
        // Add ellipsis if there's more text
        if (!isLastWord) {
            nextWords += "..."
        }

        // --- Extract text preceding the match ---
        isLastWord = false
        var prevWords = ""
        
        // Work with reversed text to find preceding words more easily
        var curPrevIndex = text.length - startIndex
        val reversedText = text.reversed()
        
        // If match doesn't start at a space, find the beginning of the current word
        if (reversedText[curPrevIndex] != ' ') {
            curPrevIndex = reversedText.indexOf(' ', startIndex = curPrevIndex)
        }
        
        // Store the start of the matched text/word
        val newStartIndex = reversedText.length - curPrevIndex
        curPrevIndex++
        
        // Extract up to 3 words preceding the match
        for (numWord in 0..2) {
            val prevSpaceIndex = reversedText.indexOf(' ', startIndex = curPrevIndex)
            if (prevSpaceIndex != -1) {
                // Add word to context (reverse it again)
                prevWords = reversedText.substring(curPrevIndex..<prevSpaceIndex).reversed() + " " + prevWords
                curPrevIndex = prevSpaceIndex + 1
            } else {
                // We've reached the beginning of the text
                isLastWord = true
                break
            }
        }
        
        // Add ellipsis if there's more text
        if (!isLastWord) {
            prevWords = "...$prevWords"
        }

        // Create and return the search result item
        return FullSearchResultItem(song,
                                    prevWords,
                                    text.substring(newStartIndex, newEndIndex),
                                    nextWords)
    }
}