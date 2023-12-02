package ru.petr.songapp.commonAndroid.settingsComponent

import com.arkivanov.decompose.value.Value

interface SettingsComponent {

    val songFontSize: Value<Int>
    val proModeIsActive: Value<Boolean>

    fun storeSongFontSize(value: Int)
    fun storeProModeIsActive(value: Boolean)
}