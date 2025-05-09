package ru.petr.songapp.commonAndroid.settingsComponent

import android.content.Context
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.petr.songapp.R
import ru.petr.songapp.database.datastore.settings.SettingsStore

class DefaultSettingsComponent(
    context: Context
) : SettingsComponent {

    private val defaultFontSize = context.resources.getInteger(R.integer.default_font_size)
    private val defaultProMode = context.resources.getBoolean(R.bool.default_pro_mode)

    private val _songFontSize: MutableValue<Int> = MutableValue(defaultFontSize)
    override val songFontSize: Value<Int> = _songFontSize

    private val _proModeIsActive: MutableValue<Boolean> = MutableValue(defaultProMode)
    override val proModeIsActive: Value<Boolean> = _proModeIsActive

    private val settingsStore = SettingsStore(context)

    private val scope = CoroutineScope(Job())

    init {
        scope.launch {
            settingsStore.getIntSetting(Settings.SONG_FONT_SIZE.settingName, defaultFontSize).collect { fontSize ->
                _songFontSize.update { fontSize }
            }
        }
        scope.launch {
            settingsStore.getBooleanSetting(Settings.PRO_MODE_IS_ACTIVE.settingName, defaultProMode).collect { proMode ->
                _proModeIsActive.update { proMode }
            }
        }
    }

    override fun storeSongFontSize(value: Int) {
        scope.launch {
            settingsStore.storeIntSetting(Settings.SONG_FONT_SIZE.settingName, value)
        }
    }

    override fun storeProModeIsActive(value: Boolean) {
        scope.launch {
            settingsStore.storeBooleanSetting(Settings.PRO_MODE_IS_ACTIVE.settingName, value)
        }
    }
}

enum class Settings(val settingName: String) {
    SONG_FONT_SIZE("song_font_size"),
    PRO_MODE_IS_ACTIVE("pro_mode_is_active")
}