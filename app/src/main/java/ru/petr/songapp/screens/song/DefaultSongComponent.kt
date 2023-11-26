package ru.petr.songapp.screens.song

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.petr.songapp.database.room.SongAppDB
import ru.petr.songapp.screens.song.models.Song
import ru.petr.songapp.screens.song.models.parsing.SongBuilder.getSong

class DefaultSongComponent(
    componentContext: ComponentContext,
    val database: SongAppDB,
    val collectionId: Int,
    val songId: Int,
) : SongComponent, ComponentContext by componentContext {

    override val song: MutableValue<Song> = MutableValue(Song.emptySong)

    init {
        CoroutineScope(Job()).launch {
            database.SongDao().getSongWithCollectionById(songId).collect { songFromDB ->
                song.update { getSong(songFromDB) }
            }
        }

    }
}