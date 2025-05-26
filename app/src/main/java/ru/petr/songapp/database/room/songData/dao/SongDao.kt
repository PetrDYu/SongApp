package ru.petr.songapp.database.room.songData.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.petr.songapp.database.room.songData.SongDBModel
import ru.petr.songapp.database.room.songData.SongWithCollectionFromDB

/**
 * Data Access Object (DAO) interface for handling song-related database operations.
 * Provides methods for CRUD operations on songs in the database.
 */
@Dao
interface SongDao {
    /**
     * Inserts a new song into the database
     * @param songDBModel Song model to be inserted
     * @return ID of the newly inserted song
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(songDBModel: SongDBModel): Long

    /**
     * Retrieves all songs belonging to a specific collection
     * @param collectionId ID of the collection to fetch songs from
     * @return Flow of songs list from the specified collection
     */
    @Query("SELECT Id, NumberInCollection, Name FROM Songs WHERE CollectionId = :collectionId ORDER BY NumberInCollection")
    fun getCollectionSongs(collectionId: Int): Flow<List<SongDataForCollection>>

    /**
     * Retrieves all songs marked as favorite
     * @return Flow of favorite songs list
     */
    @Query("SELECT Id, NumberInCollection, Name FROM Songs WHERE isFavorite = 1 ORDER BY NumberInCollection")
    fun getAllFavoriteSongs(): Flow<List<SongDataForCollection>>

    /**
     * Retrieves a specific song by its ID
     * @param id ID of the song to retrieve
     * @return Flow containing the requested song
     */
    @Query("SELECT * FROM Songs WHERE Id = :id LIMIT 1")
    fun getSongById(id: Int): Flow<SongDBModel>

    /**
     * Retrieves a song with its collection information by song ID
     * @param id ID of the song to retrieve
     * @return Flow containing the song with collection data
     */
    @Transaction
    @Query("SELECT * FROM Songs WHERE Id= :id LIMIT 1")
    fun getSongWithCollectionById(id: Int): Flow<SongWithCollectionFromDB>

    /**
     * Gets the total count of songs in the database
     * @return Flow containing the count of songs
     */
    @Query("SELECT COUNT(*) FROM Songs")
    fun getSongsCount(): Flow<Int>

    /** Retrieves the ID of a song based on its number in a collection and the collection ID
     * @param numberInCollection Song number within the collection
     * @param collectionId ID of the collection
     * @return Flow containing the ID of the song
     */
    @Query("SELECT Id FROM Songs WHERE NumberInCollection = :numberInCollection AND CollectionId = :collectionId")
    fun getSongIdByNumAndCollection(numberInCollection: Int, collectionId: Int): Flow<List<Int>>

    /**
     * Updates an existing song in the database
     * @param songDBModel Updated song model
     */
    @Update
    suspend fun update(songDBModel: SongDBModel)

    /**
     * Updates the favorite status of a song
     * @param id ID of the song to update
     * @param isFavorite New favorite status
     */
    @Query("UPDATE Songs SET isFavorite = :isFavorite WHERE Id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    /**
     * Deletes a song from the database
     * @param songDBModel Song model to be deleted
     */
    @Delete
    suspend fun delete(songDBModel: SongDBModel)
}

/**
 * Data class representing simplified song information for collection display.
 * Contains only essential song data needed for list views.
 *
 * @property id Unique identifier for the song
 * @property numberInCollection Song number within its collection
 * @property name Title of the song
 */
data class SongDataForCollection(
    @ColumnInfo(name = "Id")
    val id: Int,

    @ColumnInfo(name = "NumberInCollection")
    val numberInCollection: Int,

    @ColumnInfo(name = "Name")
    val name: String
)