package ru.petr.songapp.screens.songListScreen

import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import ru.petr.songapp.commonAndroid.databaseComponent.SongCollection
import ru.petr.songapp.screens.common.searchBar.SearchBarComponent
import ru.petr.songapp.screens.songListScreen.songList.SongListComponent

/**
 * Interface representing the Song List Screen component.
 * Manages song collections, its views and view modes.
 */
interface SongListScreenComponent {
    /**
     * List of available song collections
     */
    var collections: List<SongCollection>

    /**
     * Pages containing song list components for navigation
     */
    val collectionPages: Value<ChildPages<*, SongListComponent>>

    /**
     * Component for search functionality
     */
    val searchBarComponent: SearchBarComponent
    
    /**
     * Flag indicating whether songs are displayed in grid mode
     */
    val isInGridMode: Value<Boolean>

    /**
     * Selects a collection by its ID
     * @param id ID of the collection to select
     */
    fun selectCollectionById(id: Int)

    /**
     * Selects a collection by its index in the collections list
     * @param index Index of the collection to select
     */
    fun selectCollectionByIndex(index: Int)
    
    /**
     * Toggles between grid and list view modes for songs
     */
    fun toggleSongsViewMode()
    
    /**
     * Closes the song number grid view
     */
    fun closeSongNumberGrid()
}