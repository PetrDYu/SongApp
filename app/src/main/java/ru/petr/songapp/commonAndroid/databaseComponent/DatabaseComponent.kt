package ru.petr.songapp.commonAndroid.databaseComponent

import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import ru.petr.songapp.database.room.songData.SongDBModel
import ru.petr.songapp.database.room.songData.dao.SongDataForCollection

/**
 * Interface responsible for managing database operations related to songs and collections.
 * Provides methods for retrieving, updating and managing song data.
 */
interface DatabaseComponent {
    /**
     * List of available song collections
     */
    val collections: Value<List<SongCollection>>

    /**
     * Progress indicator for database update operations
     */
    val updatingProgress: Value<Float>

    /**
     * Flag indicating whether an update operation has completed
     */
    val updateIsFinished: Value<Boolean>

    /**
     * Retrieves all songs belonging to a specific collection
     * @param collectionId ID of the collection to fetch songs from
     * @return Value object containing a list of songs in the collection
     */
    fun getAllSongsInCollection(collectionId: Int): Value<List<SongDataForCollection>>

    /**
     * Retrieves a song by its unique ID
     * @param id ID of the song to retrieve
     * @return The requested song database model
     */
    suspend fun getSongById(id: Int): SongDBModel

    /**
     * Gets a Value wrapper for a song by its ID
     * @param id ID of the song to retrieve
     * @return Value object containing the song database model
     */
    fun getValueSongById(id: Int): Value<SongDBModel>

    /**
     * Updates an existing song in the database
     * @param song Updated song model
     */
    fun updateSong(song: SongDBModel)

    /**
     * Updates the favorite status of a song
     * @param songId ID of the song to update
     * @param isFavorite New favorite status
     */
    fun updateSongIsFavorite(songId: Int, isFavorite: Boolean)
}

/**
 * Data class representing a song collection
 * @property id Unique identifier for the collection
 * @property name Display name of the collection
 */
@Serializable
data class SongCollection(
    val id: Int,
    val name: String,
)