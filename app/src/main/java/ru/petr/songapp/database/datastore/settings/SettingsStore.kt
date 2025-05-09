package ru.petr.songapp.database.datastore.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsStore(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
    }

    private fun <T> getSetting(key: Preferences.Key<T>, defaltValue: T): Flow<T> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaltValue
        }
    }

    private suspend fun <T> storeSetting(key: Preferences.Key<T>, settingValue: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = settingValue
        }
    }

    /*** Integer setting ***/
    fun getIntSetting(settingName: String, defaltValue: Int): Flow<Int> {
        val key = intPreferencesKey(settingName)
        return getSetting(key, defaltValue)
    }

    suspend fun storeIntSetting(settingName: String, settingValue: Int) {
        val key = intPreferencesKey(settingName)
        storeSetting(key, settingValue)
    }

    /*** String setting ***/
    fun getStringSetting(settingName: String, defaltValue: String): Flow<String> {
        val key = stringPreferencesKey(settingName)
        return getSetting(key, defaltValue)
    }

    suspend fun storeStringSetting(settingName: String, settingValue: String) {
        val key = stringPreferencesKey(settingName)
        storeSetting(key, settingValue)
    }

    /*** Boolean setting ***/
    fun getBooleanSetting(settingName: String, defaltValue: Boolean): Flow<Boolean> {
        val key = booleanPreferencesKey(settingName)
        return getSetting(key, defaltValue)
    }

    suspend fun storeBooleanSetting(settingName: String, settingValue: Boolean) {
        val key = booleanPreferencesKey(settingName)
        storeSetting(key, settingValue)
    }

    /*** Float setting ***/
    fun getFloatSetting(settingName: String, defaltValue: Float): Flow<Float> {
        val key = floatPreferencesKey(settingName)
        return getSetting(key, defaltValue)
    }

    suspend fun storeFloatSetting(settingName: String, settingValue: Float) {
        val key = floatPreferencesKey(settingName)
        storeSetting(key, settingValue)
    }
}