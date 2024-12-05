package ru.petr.songapp.database.room.songData.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.petr.songapp.database.room.songData.SongDBModel
import ru.petr.songapp.database.room.songData.SongWithCollectionFromDB

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(songDBModel: SongDBModel): Long

    @Query("SELECT Id, NumberInCollection, Name FROM Songs WHERE CollectionId = :collectionId ORDER BY NumberInCollection")
    fun getCollectionSongs(collectionId: Int): Flow<List<SongDataForCollection>>

    @Query("SELECT Id, NumberInCollection, Name FROM Songs WHERE isFavorite = 1 ORDER BY NumberInCollection")
    fun getAllFavoriteSongs(): Flow<List<SongDataForCollection>>

    @Query("SELECT * FROM Songs WHERE Id = :id LIMIT 1")
    fun getSongById(id: Int): Flow<SongDBModel>

    @Transaction
    @Query("SELECT * FROM Songs WHERE Id= :id LIMIT 1")
    fun getSongWithCollectionById(id: Int): Flow<SongWithCollectionFromDB>

    @Query("SELECT COUNT(*) FROM Songs")
    fun getSongsCount(): Flow<Int>

    @Update
    suspend fun update(songDBModel: SongDBModel)

    @Query("UPDATE Songs SET isFavorite = :isFavorite WHERE Id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Delete
    suspend fun delete(songDBModel: SongDBModel)
}

data class SongDataForCollection(
    @ColumnInfo(name = "Id")
    val id: Int,

    @ColumnInfo(name = "NumberInCollection")
    val numberInCollection: Int,

    @ColumnInfo(name = "Name")
    val name: String
    )