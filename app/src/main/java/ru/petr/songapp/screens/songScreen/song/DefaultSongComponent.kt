package ru.petr.songapp.screens.songScreen.song

import androidx.compose.ui.geometry.Offset
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import ru.petr.songapp.commonAndroid.databaseComponent
import ru.petr.songapp.commonAndroid.settings
import ru.petr.songapp.screens.songScreen.song.models.Song
import ru.petr.songapp.screens.songScreen.song.models.parsing.SongBuilder.getSong

class DefaultSongComponent(
    componentContext: ComponentContext,
    private val collectionId: Int,
    override val songId : Int,
) : SongComponent, ComponentContext by componentContext {

    private val _song: MutableValue<Song> = MutableValue(Song.emptySong)
    override val song: Value<Song> = _song
    override val fontSize: Value<Int> = settings.songFontSize

    private val _name = MutableValue("")
    override val name: Value<String> = _name

    private val _numberInCollection = MutableValue(0)
    override val numberInCollection: Value<Int> = _numberInCollection

    private val _isFavorite = MutableValue(false)
    override val isFavorite: Value<Boolean> = _isFavorite

    private val _chorusOffset = MutableValue(Offset(0f, 0f))
    override val chorusOffset: Value<Offset> = _chorusOffset

    init {
        databaseComponent.getValueSongById(songId).observe { songFromDB ->
            _song.update { getSong(songFromDB) }
            _name.update { songFromDB.songData.name }
            _numberInCollection.update { songFromDB.songData.numberInCollection }
            _isFavorite.update { songFromDB.songData.isFavorite }
        }
    }
}