package ru.petr.songapp.screens.collections

import com.arkivanov.decompose.value.Value
import ru.petr.songapp.database.room.songData.SongCollectionDBModel

interface CollectionsComponent {

    val songCollections: Value<List<SongCollectionDBModel>>

    fun onSongCollectionClicked(id: Int)
}