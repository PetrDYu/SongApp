package ru.petr.songapp.screens.collections

import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import ru.petr.songapp.database.room.songData.SongCollectionDBModel

interface CollectionsComponent : BackHandlerOwner {

    val songCollections: Value<List<SongCollectionDBModel>>

    fun onSongCollectionClicked(id: Int)

    fun onBackClicked()

//    data class SongCollectionItem(
//        val id: Int,
//        val title: String
//    )
}