package ru.petr.songapp.commonAndroid

import com.arkivanov.decompose.ComponentContext
import ru.petr.songapp.SongApp
import ru.petr.songapp.commonAndroid.settingsComponent.SettingsComponent
import ru.petr.songapp.database.room.SongAppDB


val ComponentContext.database: SongAppDB
    get() = SongApp.database

val ComponentContext.settings: SettingsComponent
    get() = SongApp.settings