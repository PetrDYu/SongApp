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

    override val songCollections: Value<List<SongCollection>> = databaseComponent.collections

    override val dbUpdatingProgress: Value<Float> = databaseComponent.updatingProgress

    override val dbUpdateIsFinished: Value<Boolean> = databaseComponent.updateIsFinished

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