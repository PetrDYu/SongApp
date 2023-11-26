package ru.petr.songapp.database.room.songData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "SongCollections")
@Serializable
data class SongCollectionDBModel(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "Id") val id: Int,
    @ColumnInfo(name = "Name") val name: String,
    @ColumnInfo(name = "ShortName") val shortName: String
)
