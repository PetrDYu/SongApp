package ru.petr.songapp.screens.songScreen

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import ru.petr.songapp.screens.songScreen.settingsSheet.SettingsSheetComponent
import ru.petr.songapp.screens.songScreen.song.SongComponent

interface SongScreenComponent {
    val song: SongComponent
    val settingsSheet: Value<ChildSlot<*,SettingsSheetComponent>>
    val prevButtonIsNeeded: Value<Boolean>
    val nextButtonIsNeeded: Value<Boolean>


    fun showSettingsSheet()
    fun setIsFavorite(isFavorite: Boolean)
    fun onChangeSongClicked(isNext: Boolean)
}