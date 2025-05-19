package ru.petr.songapp.screens.songScreen

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import ru.petr.songapp.screens.songScreen.settingsSheet.SettingsSheetComponent
import ru.petr.songapp.screens.songScreen.song.SongComponent

interface SongScreenComponent {
    val song: SongComponent
    val settingsSheet: Value<ChildSlot<*,SettingsSheetComponent>>
    val prevButtonIsNeeded: Boolean
    val nextButtonIsNeeded: Boolean
    val onChangeSongClicked: (songNumber: Int) -> Unit

    fun showSettingsSheet()
    fun setIsFavorite(isFavorite: Boolean)
}