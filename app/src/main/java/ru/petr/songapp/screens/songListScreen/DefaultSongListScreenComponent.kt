package ru.petr.songapp.screens.songListScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.backhandler.BackCallback
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import ru.petr.songapp.commonAndroid.databaseComponent
import ru.petr.songapp.commonAndroid.databaseComponent.SongCollection
import ru.petr.songapp.database.room.songData.utils.removeSpecialSymbolsAndGetPositions
import ru.petr.songapp.screens.common.searchBar.DefaultSearchBarComponent
import ru.petr.songapp.screens.common.searchBar.SearchBarComponent
import ru.petr.songapp.screens.songListScreen.settingsDialog.DefaultSongListSettingsDialogComponent
import ru.petr.songapp.screens.songListScreen.settingsDialog.SongListSettingsDialogComponent
import ru.petr.songapp.screens.songListScreen.songList.DefaultSongListComponent
import ru.petr.songapp.screens.songListScreen.songList.SongListComponent

/**
 * Default implementation of the SongListScreenComponent interface.
 * Manages collections, song lists, search functionality, and view modes.
 * 
 * @param componentContext The Decompose component context
 * @param collections List of available song collections
 * @param selectedCollectionId ID of the initially selected collection
 * @param onSongSelect Callback function invoked when a song is selected
 */
class DefaultSongListScreenComponent(
    componentContext: ComponentContext,
    override var collections: List<SongCollection>,
    private val selectedCollectionId: Int = 0,
    private val onSongSelect: (collectionId: Int, songId: Int) -> Unit,
) : SongListScreenComponent, ComponentContext by componentContext {

    /**
     * Pages navigation controller for collections
     */
    private val navigation = PagesNavigation<Config>()

    /**
     * Flag indicating whether the default collection has been set
     */
    private var defaultCollectionIsSet = false

    /**
     * Observable value for search text clicks
     */
    private val clickSearchObservable = MutableValue("")

    /**
     * Search bar component implementation that handles search functionality
     */
    override val searchBarComponent: SearchBarComponent =
        DefaultSearchBarComponent(childContext("SearchBar")) { searchText ->
            closeSongNumberGrid()
            clickSearchObservable.update { removeSpecialSymbolsAndGetPositions(searchText.trim()).first }
        }

    /**
     * Settings dialog component implementation
     */
    override val settingsDialogComponent: SongListSettingsDialogComponent =
        DefaultSongListSettingsDialogComponent(childContext("settings_dialog"))

    /**
     * Mutable storage for grid mode state with state restoration
     */
    private val _isInGridMode = MutableValue(stateKeeper.consume("is_in_grid_mode", Boolean.serializer()) == true)
    
    /**
     * Read-only access to grid mode state
     */
    override val isInGridMode: Value<Boolean> = _isInGridMode


    /**
     * Back button callback to handle exiting grid mode
     */
    private val backCallback = BackCallback(false) {
        _isInGridMode.update { false }
    }

    /**
     * Collection pages containing song list components for navigation
     */
    override var collectionPages: Value<ChildPages<*, SongListComponent>> =
        childPages(
            source = navigation,
            serializer = Config.serializer(),
            initialPages = {
                Pages(
                    items = List(collections.size) { Config(collections[it].id) },
                    selectedIndex = getIndexById(selectedCollectionId)
                )
            }
        ) { config, childComponentContext ->
            DefaultSongListComponent(
                componentContext = childComponentContext,
                collectionId = config.collectionId,
                searchIsActive = searchBarComponent.searchIsActive,
                clickSearchObservable,
                isInGridMode = isInGridMode
            ) { songId -> onSongSelect(config.collectionId, songId) }
        }

    init {
        // Register for state saving of grid mode
        stateKeeper.register(
            key = "is_in_grid_mode",
            strategy = Boolean.serializer()
        ) { _isInGridMode.value }

        // Subscribe to collection changes
        databaseComponent.collections.subscribe { newCollections ->
            if (defaultCollectionIsSet) {
                val oldSelectedIndex = collectionPages.value.selectedIndex
                collectionPages =
                    childPages(
                        source = navigation,
                        serializer = Config.serializer(),
                        initialPages = {
                            Pages(
                                items = List(newCollections.size) { index -> Config(newCollections[index].id) },
                                selectedIndex = collectionPages.value.selectedIndex
                            )
                        },
                        key = "SongListPager${(0..10000).random()}"
                    ) { config, childComponentContext ->
                        DefaultSongListComponent(
                            componentContext = childComponentContext,
                            collectionId = config.collectionId,
                            searchIsActive = searchBarComponent.searchIsActive,
                            clickSearchObservable,
                            isInGridMode = isInGridMode
                        ) { songId -> onSongSelect(config.collectionId, songId) }
                    }
                collections = newCollections
                selectCollectionByIndex(oldSelectedIndex)
            } else {
                // Select first collection page
                selectCollectionById(selectedCollectionId)
                defaultCollectionIsSet = true
            }
        }

        // Enable/disable back handler based on grid mode state
        _isInGridMode.subscribe {
            backCallback.isEnabled = _isInGridMode.value
        }

        // Register back handler
        backHandler.register(backCallback)
    }

    /**
     * Selects a collection page by its collection ID
     * @param id ID of the collection to select
     */
    override fun selectCollectionById(id: Int) {
        val index = getIndexById(id)
        navigation.select(index = if (index != -1) index else 0)
    }

    /**
     * Selects a collection page by its index in the collections list
     * @param index Index of the collection to select
     */
    override fun selectCollectionByIndex(index: Int) {
        navigation.select(index = index)
    }

    /**
     * Toggles between grid and list view modes for songs.
     * If search is active and grid mode is enabled, closes the grid.
     */
    override fun toggleSongsViewMode() {
        if (!searchBarComponent.searchIsActive.value) {
            _isInGridMode.update { !it }
        } else if (isInGridMode.value) {
            closeSongNumberGrid()
        }
    }

    /**
     * Closes the song number grid view
     */
    override fun closeSongNumberGrid() {
        _isInGridMode.update { false }
    }

    /**
     * Finds the index of a collection by its ID
     * @param id ID of the collection to find
     * @return Index of the collection or -1 if not found
     */
    private fun getIndexById(id: Int): Int {
        return collections.indexOfFirst { it.id == id }
    }

    /**
     * Serializable configuration for collection pages
     * @property collectionId ID of the collection associated with this configuration
     */
    @Serializable // kotlinx-serialization plugin must be applied
    private data class Config(val collectionId: Int)
}
