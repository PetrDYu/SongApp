package ru.petr.songapp.commonAndroid.databaseComponent

import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import ru.petr.songapp.database.room.songData.SongDBModel
import ru.petr.songapp.database.room.songData.dao.SongDataForCollection

interface DatabaseComponent {
    val collections: Value<List<SongCollection>>

    fun getAllSongsInCollection(collectionId: Int): Value<List<SongDataForCollection>>

    suspend fun getSongById(id: Int): SongDBModel

    fun getValueSongById(id: Int): Value<SongDBModel>

    fun updateSong(song: SongDBModel)

    fun updateSongIsFavorite(songId: Int, isFavorite: Boolean)
}

@Serializable
data class SongCollection(
    val id: Int,
    val name: String,
)