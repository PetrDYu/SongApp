package ru.petr.songapp.database.room.songData

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation
import ru.petr.songapp.database.room.songData.SongCollectionDBModel
import ru.petr.songapp.database.room.songData.SongData

data class SongWithCollectionFromDB (

    @ColumnInfo(name = "Id")
    val id: Int,

    @ColumnInfo(name = "CollectionId")
    val collectionId: Int,

    @Relation(parentColumn = "CollectionId", entityColumn = "Id")
    val collection: SongCollectionDBModel,

    @Embedded
    val songData: SongData,

    @ColumnInfo(name = "Body")
    val body: String,
)