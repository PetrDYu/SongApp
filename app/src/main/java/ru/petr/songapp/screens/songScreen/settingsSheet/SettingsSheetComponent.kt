package ru.petr.songapp.screens.songScreen.settingsSheet

import com.arkivanov.decompose.value.Value

interface SettingsSheetComponent {

    val fontSize: Value<Int>

    fun onDismissRequest()
    fun onFontSizeChanged(newSize: Int)
}