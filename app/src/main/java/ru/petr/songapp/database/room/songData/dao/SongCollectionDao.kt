package ru.petr.songapp.database.room.songData.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.petr.songapp.database.room.songData.SongCollectionDBModel

@Dao
interface SongCollectionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(collection: SongCollectionDBModel): Long

    @Query("SELECT * FROM SongCollections")
    fun getAllCollections(): Flow<List<SongCollectionDBModel>>

    @Query("SELECT * FROM SongCollections WHERE Id = :id")
    fun getById(id: Int): Flow<SongCollectionDBModel>

    @Update
    fun update(collection: SongCollectionDBModel)

    @Delete
    fun delete(collection: SongCollectionDBModel)
}