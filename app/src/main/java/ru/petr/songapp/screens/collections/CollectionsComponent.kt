package ru.petr.songapp.screens.collections

import com.arkivanov.decompose.value.Value
import ru.petr.songapp.commonAndroid.databaseComponent.SongCollection

interface CollectionsComponent {

    val songCollections: Value<List<SongCollection>>

    val dbUpdatingProgress: Value<Float>

    val dbUpdateIsFinished: Value<Boolean>

    fun onSongCollectionClicked(id: Int)
}