package ru.petr.songapp.screens.collections

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.petr.songapp.database.room.songData.SongCollectionDBModel

class PreviewCollectionsComponent() : CollectionsComponent {
    override val songCollections: Value<List<SongCollectionDBModel>> =
        MutableValue(listOf(
            SongCollectionDBModel(0, "Избранное", "Избр"),
            SongCollectionDBModel(1, "Будем петь и славить", "БПиС"),
            SongCollectionDBModel(2, "Песнь возрождения", "ПВ"),
        ))

    override fun onSongCollectionClicked(id: Int) {}
}