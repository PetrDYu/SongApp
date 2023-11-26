package ru.petr.songapp.database.room

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.petr.songapp.database.room.songData.SongDBModel
import ru.petr.songapp.database.room.songData.SongCollectionDBModel
import ru.petr.songapp.database.room.songData.dao.SongCollectionDao
import ru.petr.songapp.database.room.songData.dao.SongDao
import ru.petr.songapp.database.room.songData.utils.checkOpenedDB
import ru.petr.songapp.database.room.songData.utils.populateDBFromAssets

@Database(entities = [SongDBModel::class, SongCollectionDBModel::class], version = 1, exportSchema = true)
abstract class SongAppDB() : RoomDatabase() {
    abstract fun SongDao(): SongDao
    abstract fun SongCollectionDao(): SongCollectionDao

    var creatingJob: Job? = null

    private class SongAppDBCallback(
        private val scope: CoroutineScope,
        val appContext: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                database.creatingJob = scope.launch {
                    populateDBFromAssets(appContext, database)
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                checkOpenedDB(appContext, database)
            }
            Log.d("DB", "opened")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SongAppDB? = null

        fun getDB(context: Context, scope: CoroutineScope): SongAppDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SongAppDB::class.java,
                    "SongAppDB"
                )
                    .addCallback(SongAppDBCallback(scope, appContext = context))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}