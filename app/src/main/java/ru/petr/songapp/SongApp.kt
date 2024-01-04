package ru.petr.songapp

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import ru.petr.songapp.commonAndroid.databaseComponent.DatabaseComponent
import ru.petr.songapp.commonAndroid.databaseComponent.DefaultDatabaseComponent
import ru.petr.songapp.commonAndroid.settingsComponent.DefaultSettingsComponent
import ru.petr.songapp.commonAndroid.settingsComponent.SettingsComponent
import ru.petr.songapp.database.room.SongAppDB

class SongApp : Application() {

    private val rootScope = CoroutineScope(SupervisorJob())

    private val databaseInner: SongAppDB by lazy { SongAppDB.getDB(this, rootScope) }
    private val settingsInner: SettingsComponent by
        lazy { DefaultSettingsComponent(this) }

    override fun onCreate() {
        super.onCreate()
        database = databaseInner
        databaseComponent = DefaultDatabaseComponent(this)
        settings = settingsInner
    }
    companion object {
        lateinit var database: SongAppDB
        lateinit var databaseComponent: DatabaseComponent
        lateinit var settings: SettingsComponent
    }
}