package ru.petr.songapp.screens.songListScreen

import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import ru.petr.songapp.commonAndroid.databaseComponent.SongCollection
import ru.petr.songapp.database.room.songData.dao.SongDataForCollection
import ru.petr.songapp.screens.common.searchBar.SearchBarComponent
import ru.petr.songapp.screens.songListScreen.songList.SongListComponent

interface SongListScreenComponent {
    var collections: List<SongCollection>

    val collectionPages: Value<ChildPages<*, SongListComponent>>

    val searchBarComponent: SearchBarComponent
    
    val songsByCollection: Value<Map<Int, List<SongDataForCollection>>>
    
    val isInGridMode: Value<Boolean>

    fun selectCollectionById(id: Int)

    fun selectCollectionByIndex(index: Int)
    
    fun toggleSongsViewMode()
    
    fun closeSongNumberGrid()
}