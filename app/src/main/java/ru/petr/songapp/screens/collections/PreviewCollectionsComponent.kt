package ru.petr.songapp.screens.collections

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.petr.songapp.commonAndroid.databaseComponent.SongCollection

class PreviewCollectionsComponent(dbUpdatingProgress: Float, dbUpdateIsFinished: Boolean) : CollectionsComponent {
    override val songCollections: Value<List<SongCollection>> =
        MutableValue(listOf(
            SongCollection(0, "Избранное"),
            SongCollection(1, "Будем петь и славить"),
            SongCollection(2, "Песнь возрождения"),
        ))
    override val dbUpdatingProgress: Value<Float> = MutableValue(dbUpdatingProgress)
    override val dbUpdateIsFinished: Value<Boolean> = MutableValue(dbUpdateIsFinished)

    override fun onSongCollectionClicked(id: Int) {}
}