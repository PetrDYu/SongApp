package ru.petr.songapp.screens.songListScreen.songList

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.petr.songapp.commonAndroid.databaseComponent
import ru.petr.songapp.screens.common.fullTextSearch.DefaultFullTextSearchComponent
import ru.petr.songapp.screens.common.fullTextSearch.FullSearchData
import ru.petr.songapp.screens.common.searchBar.SearchBarComponent
import ru.petr.songapp.screens.songListScreen.songList.scrollbar.DefaultScrollbarComponent
import ru.petr.songapp.screens.songListScreen.songList.scrollbar.ScrollbarComponent
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Default implementation of the SongListComponent interface.
 * Manages songs within a specific collection and handles search functionality.
 *
 * @param componentContext The Decompose component context
 * @param collectionId ID of the collection this component manages
 * @param searchIsActive Observable value indicating whether search is active
 * @param clickSearchObservable Observable value for search text input
 * @param isInGridMode Observable value indicating whether grid mode is enabled
 * @param onSongSelected Callback triggered when a song is selected
 */

class DefaultSongListComponent(
    componentContext: ComponentContext,
    collectionId: Int,
    override val searchIsActive: Value<Boolean>,
    private val clickSearchObservable: Value<String>,
    override val isInGridMode: Value<Boolean> = MutableValue(false),
    private val onSongSelected: (id: Int, songNum: Int, getSongIdByNum: (Int) -> Int) -> Unit,
) : SongListComponent, ComponentContext by componentContext {

    /**
     * Mutable container for song items in this component
     */
    private var _songItems = MutableValue(listOf<SongListComponent.SongItem>())
    
    /**
     * Read-only access to song items as an observable value
     */
    override val songItems: Value<List<SongListComponent.SongItem>> = _songItems


    /**
     * Full text search component for searching within song lyrics
     */
    private val fullSearch = DefaultFullTextSearchComponent(childContext("DefaultFullTextSearchComponent"),
                                                            collectionId,
                                                            songItems)

    /**
     * Provides access to full text search functionality data and results
     */
    override val fullTextSearchData: FullSearchData = fullSearch.searchData

    /**
     * Scrollbar component for quick navigation through song list
     */
    override val scrollbar: ScrollbarComponent = DefaultScrollbarComponent(childContext("DefaultScrollbarComponent"))

    /**
     * Maintains a copy of the song items for filtered searches
     */
    private var _songItemsCopy = _songItems.value

    /**
     * Coroutine scope for running operations with lifecycle awareness
     */
    private val scope = CoroutineScope(EmptyCoroutineContext + Job())

    init {
        // Subscribe to search text changes and update the filtered list accordingly
        clickSearchObservable.subscribe { searchText ->
            if (searchText.isBlank()) return@subscribe
            scope.launch {
                _songItems.update { SearchBarComponent.updateSongList(_songItemsCopy, searchText) }
                fullSearch.activateSearch(true, searchText)
            }
        }

        // Toggle scrollbar visibility based on search state
        searchIsActive.subscribe { isActive ->
            if (isActive) {
                // If search is active, scrollbar is not needed
                scrollbar.scrollbarNeed(false)
            }
            else {
                // If search is not active, show scrollbar
                scrollbar.scrollbarNeed(true)
                _songItems.update { _songItemsCopy }
                fullSearch.activateSearch(false)
            }
        }

        // Subscribe to database changes and update song list when collection changes
        databaseComponent.getAllSongsInCollection(collectionId).subscribe {
            val newList = mutableListOf<SongListComponent.SongItem>()
            for(song in it) {
                newList.add(
                        SongListComponent.SongItem(
                                song.id,
                                song.numberInCollection,
                                song.name))
            }

            _songItemsCopy = newList

            if (searchIsActive.value) {
                // If search is active, update the filtered list
                scope.launch {
                    _songItems.update {
                        SearchBarComponent.updateSongList(
                            newList,
                            clickSearchObservable.value)
                    }
                }
            } else {
                _songItems.update { newList }
            }
        }

        // Update scrollbar with song numbers whenever the song list changes
        _songItems.subscribe { songList ->
            scrollbar.setItemNumbersList(songList.map { it.numInColl })
        }

        // Hide scrollbar in grid mode
        isInGridMode.subscribe { inGridMode ->
            scrollbar.scrollbarNeed(!inGridMode)
        }

        // Cancel all coroutines when component is destroyed
        lifecycle.doOnDestroy {
            scope.coroutineContext[Job]?.cancel()
        }
    }


    /**
     * Handles when a song is clicked by invoking the provided song selection callback
     * @param id ID of the clicked song
     */
    override fun onSongClicked(id: Int) {
        onSongSelected(id, songItems.value.find { it.id == id }!!.numInColl, ::getSongIdByNum)
    }

    /**
     * Initiates full text search with the current search text
     */
    override fun onFullTextSearch() {
        fullSearch.activateSearch(true, clickSearchObservable.value)
    }

    private fun getSongIdByNum(num: Int): Int {
        val songId = songItems.value.find { it.numInColl == num }?.id
        if (songId == null)
        {
            return if (num >= songItems.value.size) {
                songItems.value.last().id
            } else {
                songItems.value.first().id
            }
        }
        return songId
    }
}