package ru.petr.songapp.screens.collections

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import ru.petr.songapp.commonAndroid.databaseComponent
import ru.petr.songapp.commonAndroid.databaseComponent.SongCollection

class DefaultCollectionsComponent(
    componentContext: ComponentContext,
    private val onCollectionSelected: (id: Int, collections: List<SongCollection>) -> Unit,
) : CollectionsComponent, ComponentContext by componentContext {

//    private var _songCollections = MutableValue(listOf<SongCollection>())

    override val songCollections: Value<List<SongCollection>> = databaseComponent.collections

//    init {
//        CoroutineScope(Job()).launch {
//            database.SongCollectionDao().getAllCollections().collect {
//                _songCollections.value = it
//            }
//        }
//    }

    override fun onSongCollectionClicked(id: Int) {
        onCollectionSelected(id, songCollections.value)
    }

    @Serializable
    private data class Config (
        @Serializable
        val index: Int,

        @Serializable
        val isBackEnabled: Boolean,
    )
}