package ru.petr.songapp.commonAndroid.databaseComponent

import com.arkivanov.decompose.value.Value
import ru.petr.songapp.database.room.songData.SongDBModel
import ru.petr.songapp.database.room.songData.dao.SongDataForCollection

interface DatabaseComponent {
    val collections: Value<List<SongCollection>>

    fun getAllSongsInCollection(collectionId: Int): Value<List<SongDataForCollection>>

    fun updateSong(song: SongDBModel)

    fun updateSongIsFavorite(songId: Int, isFavorite: Boolean)
}

data class SongCollection(
    val id: Int,
    val name: String,
)