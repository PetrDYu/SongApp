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

class DefaultSongListComponent(
    componentContext: ComponentContext,
    collectionId: Int,
    override val searchIsActive: Value<Boolean>,
    private val clickSearchObservable: Value<String>,
    private val onSongSelected: (id: Int) -> Unit,
) : SongListComponent, ComponentContext by componentContext {

    private var _songItems = MutableValue(listOf<SongListComponent.SongItem>())
    override val songItems: Value<List<SongListComponent.SongItem>> = _songItems

    private val fullSearch = DefaultFullTextSearchComponent(childContext("DefaultFullTextSearchComponent"),
                                                            collectionId,
                                                            songItems)

    override val fullTextSearchData: FullSearchData = fullSearch.searchData

    override val scrollbar: ScrollbarComponent = DefaultScrollbarComponent(childContext("DefaultScrollbarComponent"))

    private var _songItemsCopy = _songItems.value

    init {
        clickSearchObservable.subscribe { searchText ->
            if (searchText.isBlank()) return@subscribe
            CoroutineScope(Job()).launch {
                _songItems.update { SearchBarComponent.updateSongList(_songItemsCopy, searchText) }
                fullSearch.activateSearch(true, searchText)
            }
        }

        searchIsActive.subscribe { isActive ->
            if (isActive) {
                // Если поиск активен, то скроллбар не требуется
                scrollbar.scrollbarNeed(false)
            }
            else {
                // Если поиск не активен, то показываем скроллбар
                scrollbar.scrollbarNeed(true)
                _songItems.update { _songItemsCopy }
                fullSearch.activateSearch(false)
            }
        }

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
                CoroutineScope(Job()).launch {
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

        _songItems.subscribe { songList ->
            scrollbar.setItemNumbersList(songList.map { it.numInColl })
        }
    }


    override fun onSongClicked(id: Int) {
        onSongSelected(id)
    }

    override fun onFullTextSearch() {
        fullSearch.activateSearch(true, clickSearchObservable.value)
    }
}