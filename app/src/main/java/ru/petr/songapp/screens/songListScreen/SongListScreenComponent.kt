package ru.petr.songapp.screens.songListScreen

import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import ru.petr.songapp.database.room.songData.SongCollectionDBModel
import ru.petr.songapp.screens.common.searchBar.SearchBarComponent
import ru.petr.songapp.screens.songListScreen.songList.SongListComponent

interface SongListScreenComponent {
    var collections: List<SongCollectionDBModel>

    val collectionPages: Value<ChildPages<*, SongListComponent>>

    val searchBarComponent: SearchBarComponent

    fun selectCollectionById(id: Int)

    fun selectCollectionByIndex(index: Int)
}