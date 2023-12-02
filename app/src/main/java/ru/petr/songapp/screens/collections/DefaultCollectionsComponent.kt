package ru.petr.songapp.screens.collections


import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import ru.petr.songapp.database.room.songData.SongCollectionDBModel
import ru.petr.songapp.commonAndroid.database

class DefaultCollectionsComponent(
    componentContext: ComponentContext,
    private val onCollectionSelected: (id: Int, collections: List<SongCollectionDBModel>) -> Unit,
) : CollectionsComponent, ComponentContext by componentContext {

    private var _songCollections = MutableValue(listOf<SongCollectionDBModel>())

    override val songCollections: Value<List<SongCollectionDBModel>> = _songCollections

    init {
        CoroutineScope(Job()).launch {
            database.SongCollectionDao().getAllCollections().collect {
                _songCollections.value = it;
            }
        }
    }

    override fun onSongCollectionClicked(id: Int) {
        onCollectionSelected(id, _songCollections.value)
    }

    override fun onBackClicked() {
        TODO("Not yet implemented")
    }

    @Serializable
    private data class Config (
        @Serializable
        val index: Int,

        @Serializable
        val isBackEnabled: Boolean,
    )
}