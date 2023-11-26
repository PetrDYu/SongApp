package ru.petr.songapp.database.room.songData.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.petr.songapp.database.room.SongAppDB
import ru.petr.songapp.database.room.songData.dao.SongDataForCollection

fun checkOpenedDB(appContext: Context, database: SongAppDB) {
    CoroutineScope(Dispatchers.IO).launch {
        database.creatingJob?.join()
        val songCollections = database.SongCollectionDao().getAllCollections().first().map { it.id to it.name }
        val songIdsByCols = mutableMapOf<String, Pair<Int, List<SongDataForCollection>>>()
        for ((songColId, songColName) in songCollections) {
            songIdsByCols[songColName] = songColId to database.SongDao().getCollectionSongs(songColId).first()
        }
        populateDBFromAssets(appContext, database, songIdsByCols)
        Log.d("DB", "$songIdsByCols")
    }
}