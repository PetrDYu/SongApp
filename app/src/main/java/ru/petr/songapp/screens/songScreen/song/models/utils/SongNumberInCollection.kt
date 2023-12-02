package ru.petr.songapp.screens.songScreen.song.models.utils

import ru.petr.songapp.database.room.songData.SongCollectionDBModel

class SongNumberInCollection(number: Int, collection: SongCollectionDBModel) {
    val mNumber: Int
    val mCollection: SongCollectionDBModel

    init {
        mCollection = collection
        mNumber = number
    }
}