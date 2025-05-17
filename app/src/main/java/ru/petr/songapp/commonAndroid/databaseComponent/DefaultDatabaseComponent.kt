package ru.petr.songapp.commonAndroid.databaseComponent

import android.content.Context
import android.util.Log
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.petr.songapp.database.room.SongAppDB
import ru.petr.songapp.database.room.songData.SongCollectionDBModel
import ru.petr.songapp.database.room.songData.SongDBModel
import ru.petr.songapp.database.room.songData.dao.SongDataForCollection

class DefaultDatabaseComponent(
    context: Context,
    rootScope: CoroutineScope,
) : DatabaseComponent {

    private val scope = CoroutineScope(rootScope.coroutineContext + Job())
    private val database by lazy { SongAppDB.getDB(context, rootScope) }

    private val songs: MutableList<MutableValue<List<SongDataForCollection>>> = mutableListOf()
    private val songCoroutines: MutableList<Job> = mutableListOf()

    private val _collections = MutableValue(listOf<SongCollection>())
    override val collections: Value<List<SongCollection>> = _collections

    override val updatingProgress: Value<Float> = database.updatingProgress

    private val _updateIsFinished = MutableValue(false)
    override val updateIsFinished = _updateIsFinished


    override fun getAllSongsInCollection(collectionId: Int): Value<List<SongDataForCollection>> {
        val index = _collections.value.indexOfFirst { it.id == collectionId }
        return if ((songs.isNotEmpty()) && (index != -1)) {
            songs[index]
        } else {
            MutableValue(listOf())
        }
    }

    override fun getValueSongById(id: Int): Value<SongDBModel> {
        val songValue = MutableValue(SongDBModel.empty)
        scope.launch {
            database.SongDao().getSongById(id).collect { song ->
                songValue.update { song }
            }
        }
        return songValue
    }



    override suspend fun getSongById(id: Int): SongDBModel {
        return database.SongDao().getSongById(id).first()
    }

    override fun updateSong(song: SongDBModel) {
        scope.launch {
            database.SongDao().update(song)
        }
    }

    override fun updateSongIsFavorite(songId: Int, isFavorite: Boolean) {
        scope.launch {
            database.SongDao().updateFavorite(songId, isFavorite)
        }
    }

    private fun updateCollections(songCollections: List<SongCollectionDBModel>) {
        val newCollections = mutableListOf<SongCollection>()
        songCollections.forEach { collection ->
            newCollections.add(
                SongCollection(
                    collection.id,
                    collection.name
                )
            )
        }
        _collections.update { newCollections }
    }

    init {
        val collectionsIsUpdated = MutableValue(false)
        updatingProgress.subscribe { progress ->
            _updateIsFinished.update { progress.compareTo(1.0f) == 0 }
        }
        updateIsFinished.subscribe { isFinished ->
            if (isFinished) {
                scope.launch {
                    val songCollections = database.SongCollectionDao().getAllCollections().first()
                    updateCollections(songCollections)
                    collectionsIsUpdated.update { true }
                }
            }
        }
        scope.launch {
            database.SongCollectionDao().getAllCollections().collect { songCollections ->
                if (updateIsFinished.value) {
                    updateCollections(songCollections)
                    collectionsIsUpdated.update { true }
                }
            }
        }
        collectionsIsUpdated.subscribe {
            if (updateIsFinished.value && it) {
                songCoroutines.forEach { coroutine ->
                    coroutine.cancel()
                }
                songCoroutines.clear()
                songs.clear()
                _collections.value.forEachIndexed { index, collection ->
                    songs.add(MutableValue(listOf()))
                    val job = scope.launch {
                        if (collection.name == "Избранное") {
                            Log.d("DefaultDatabaseComponent", "getAllFavoriteSongs ${database.SongDao().getAllFavoriteSongs()}")
                            database.SongDao().getAllFavoriteSongs().collect { songsInCol ->
                                songs[index].update { songsInCol }
                            }
                        } else {
                            Log.d("DefaultDatabaseComponent", "getCollectionSongs(${collection.id}) ${database.SongDao().getCollectionSongs(collection.id)}")
                            database.SongDao().getCollectionSongs(collection.id).collect { songsInCol ->
                                songs[index].update { songsInCol }
                            }
                        }
                    }
                    songCoroutines.add(job)
                }
            }
        }
    }
}