package ru.petr.songapp

import android.app.Application
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import ru.petr.songapp.commonAndroid.settingsComponent.DefaultSettingsComponent
import ru.petr.songapp.commonAndroid.settingsComponent.SettingsComponent
import ru.petr.songapp.database.room.SongAppDB
import ru.petr.songapp.root.DefaultRootComponent

class SongApp : Application() {

    private val rootScope = CoroutineScope(SupervisorJob())

    private val databaseInner: SongAppDB by lazy { SongAppDB.getDB(this, rootScope) }
    private val settingsInner: SettingsComponent by
        lazy { DefaultSettingsComponent(this) }

    override fun onCreate() {
        super.onCreate()
        database = databaseInner
        settings = settingsInner
    }
    companion object {
        lateinit var database: SongAppDB
        lateinit var settings: SettingsComponent
    }
}