package ru.petr.songapp.screens.songListScreen.songList

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.petr.songapp.commonAndroid.database
import ru.petr.songapp.screens.common.searchBar.SearchBarComponent

class DefaultSongListComponent(
    componentContext: ComponentContext,
    private val collectionId: Int,
    override val searchIsActive: Value<Boolean>,
    clickSearchObservable: Value<String>,
    private val onSongSelected: (id: Int) -> Unit,
) : SongListComponent, ComponentContext by componentContext {

    private var _songItems = MutableValue(listOf<SongListComponent.SongItem>())

    override val songItems: Value<List<SongListComponent.SongItem>> = _songItems

    private var _songItemsCopy = _songItems.value

    init {
        clickSearchObservable.observe {searchText ->
            _songItems.update { SearchBarComponent.updateSongList(_songItems.value, searchText) }
        }

        searchIsActive.observe { isActive ->
            if (!isActive) {
                _songItems.update { _songItemsCopy }
            }
        }

        CoroutineScope(Job()).launch {
            componentContext.database.SongDao().getCollectionSongs(collectionId).collect {
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
                    _songItems.update { SearchBarComponent.updateSongList(newList, clickSearchObservable.value) }
                } else {
                    _songItems.update { newList }
                }

            }
        }
    }


    override fun onSongClicked(id: Int) {
        onSongSelected(id)
    }
}