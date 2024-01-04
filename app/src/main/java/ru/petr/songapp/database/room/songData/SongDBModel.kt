package ru.petr.songapp.database.room.songData

import androidx.room.*

@Entity(
    tableName = "Songs",
    foreignKeys = [
        ForeignKey(
                entity = SongCollectionDBModel::class,
                parentColumns = ["Id"],
                childColumns = ["CollectionId"]
        )
    ],
    indices = [
        Index("CollectionId"),
        Index("Name"),
        Index("CollectionId", "NumberInCollection")
    ]
)
data class SongDBModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    val id: Int,

    @ColumnInfo(name = "CollectionId")
    val collectionId: Int,

    @Embedded
    val songData: SongData,

    @ColumnInfo(name = "Body")
    val body: String,

    @ColumnInfo(name = "PlainText")
    val plainText: String,

    @ColumnInfo(name = "PlainTextWithoutSpecialSymbols")
    val plainTextWithoutSpecialSymbols: String,

    @ColumnInfo(name = "SpecialSymbolPositions")
    val specialSymbolPositions: String,
) {
    companion object {
        val empty = SongDBModel(0,
                                0,
                                SongData("", 0, false, false, "", "", "", "", false),
                                "",
                                "",
                                "",
                                "")
    }
}

data class SongData(
    @ColumnInfo(name = "Name")
    val name: String,

    @ColumnInfo(name = "NumberInCollection")
    val numberInCollection: Int,

    @ColumnInfo(name = "IsFavorite")
    val isFavorite: Boolean,

    @ColumnInfo(name = "IsCanon")
    val isCanon: Boolean,

    @ColumnInfo(name = "TextAuthors")
    val textAuthors: String,

    @ColumnInfo(name = "RusTextAuthors")
    val rusTextAuthors: String,

    @ColumnInfo(name = "MusicComposers")
    val musicComposers: String,

    @ColumnInfo(name = "AdditionalInfo")
    val additionalInfo: String,

    @ColumnInfo(name = "IsFixed")
    val isFixed: Boolean,
)