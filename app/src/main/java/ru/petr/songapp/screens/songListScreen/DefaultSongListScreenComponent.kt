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
import kotlinx.serialization.Serializable
import ru.petr.songapp.commonAndroid.databaseComponent
import ru.petr.songapp.commonAndroid.databaseComponent.SongCollection
import ru.petr.songapp.database.room.songData.utils.removeSpecialSymbolsAndGetPositions
import ru.petr.songapp.screens.common.searchBar.DefaultSearchBarComponent
import ru.petr.songapp.screens.common.searchBar.SearchBarComponent
import ru.petr.songapp.screens.songListScreen.songList.DefaultSongListComponent
import ru.petr.songapp.screens.songListScreen.songList.SongListComponent

class DefaultSongListScreenComponent(
    componentContext: ComponentContext,
    override var collections: List<SongCollection>,
    private val selectedCollectionId: Int = 0,
    private val onSongSelect: (collectionId: Int, songId: Int) -> Unit,
) : SongListScreenComponent, ComponentContext by componentContext {

    private val navigation = PagesNavigation<Config>()

    private var defaultCollectionIsSet = false

    private val clickSearchObservable = MutableValue("")

    override val searchBarComponent: SearchBarComponent =
        DefaultSearchBarComponent(childContext("SearchBar")) { searchText ->
            clickSearchObservable.update { removeSpecialSymbolsAndGetPositions(searchText.trim()).first }
        }

    override var collectionPages: Value<ChildPages<*, SongListComponent>> =
        childPages(
                source = navigation,
                serializer = Config.serializer(),
                initialPages = {
                    Pages(
                        items = List(collections.size){ Config(collections[it].id) },
                        selectedIndex = getIndexById(selectedCollectionId)
                    )
                }
        ) { config, childComponentContext ->
            DefaultSongListComponent(
                componentContext = childComponentContext,
                collectionId = config.collectionId,
                searchIsActive = searchBarComponent.searchIsActive,
                clickSearchObservable
            ) { songId -> onSongSelect(config.collectionId, songId) }
        }

    init {
        databaseComponent.collections.subscribe { newCollections ->
            if (defaultCollectionIsSet) {
                val oldSelectedIndex = collectionPages.value.selectedIndex
                collectionPages =
                    childPages(
                            source = navigation,
                            serializer = Config.serializer(),
                            initialPages = {
                                Pages(
                                        items = List(newCollections.size){ index -> Config(newCollections[index].id) },
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
                        ) { songId -> onSongSelect(config.collectionId, songId) }
                    }
                collections = newCollections
                selectCollectionByIndex(oldSelectedIndex)
            } else {
                selectCollectionById(selectedCollectionId)
                defaultCollectionIsSet = true
            }
        }
    }

    override fun selectCollectionById(id: Int) {
        val index = getIndexById(id)
        navigation.select(index = if (index != -1) index else 0)
    }

    override fun selectCollectionByIndex(index: Int) {
        navigation.select(index = index)
    }

    private fun getIndexById(id: Int) : Int {
        return collections.indexOfFirst { it.id == id }
    }



    @Serializable // kotlinx-serialization plugin must be applied
    private data class Config(val collectionId: Int)

}