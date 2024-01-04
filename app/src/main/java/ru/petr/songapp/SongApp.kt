package ru.petr.songapp

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import ru.petr.songapp.commonAndroid.databaseComponent.DatabaseComponent
import ru.petr.songapp.commonAndroid.databaseComponent.DefaultDatabaseComponent
import ru.petr.songapp.commonAndroid.settingsComponent.DefaultSettingsComponent
import ru.petr.songapp.commonAndroid.settingsComponent.SettingsComponent

class SongApp : Application() {

    private val rootScope = CoroutineScope(SupervisorJob())
    private val settingsInner: SettingsComponent by
        lazy { DefaultSettingsComponent(this) }

    override fun onCreate() {
        super.onCreate()
        databaseComponent = DefaultDatabaseComponent(this, rootScope)
        settings = settingsInner
    }
    companion object {
        lateinit var databaseComponent: DatabaseComponent
        lateinit var settings: SettingsComponent
    }
}