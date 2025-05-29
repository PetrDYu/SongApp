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
    context: Context,
    rootScope: CoroutineScope
) : SettingsComponent {

    private val defaultFontSize = context.resources.getInteger(R.integer.default_font_size)
    private val defaultProMode = context.resources.getBoolean(R.bool.default_pro_mode)
    private val defaultIsDarkTheme = context.resources.getBoolean(R.bool.default_is_dark_theme)
    private val defaultUseSystemTheme = context.resources.getBoolean(R.bool.default_use_system_theme)

    private val _songFontSize: MutableValue<Int> = MutableValue(defaultFontSize)
    override val songFontSize: Value<Int> = _songFontSize

    private val _proModeIsActive: MutableValue<Boolean> = MutableValue(defaultProMode)
    override val proModeIsActive: Value<Boolean> = _proModeIsActive

    private val _isDarkTheme: MutableValue<Boolean> = MutableValue(defaultIsDarkTheme)
    override val isDarkTheme: Value<Boolean> = _isDarkTheme

    private val _useSystemTheme: MutableValue<Boolean> = MutableValue(defaultUseSystemTheme)
    override val useSystemTheme: Value<Boolean> = _useSystemTheme

    private val settingsStore = SettingsStore(context)

    private val scope = CoroutineScope(rootScope.coroutineContext + Job())

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

        scope.launch {
            settingsStore.getBooleanSetting(Settings.IS_DARK_THEME.settingName, defaultIsDarkTheme).collect { isDarkTheme ->
                _isDarkTheme.update { isDarkTheme }
            }
        }

        scope.launch {
            settingsStore.getBooleanSetting(Settings.USE_SYSTEM_THEME.settingName, defaultUseSystemTheme).collect { useSystemTheme ->
                _useSystemTheme.update { useSystemTheme }
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

    override fun storeIsDarkTheme(value: Boolean) {
        scope.launch {
            settingsStore.storeBooleanSetting(Settings.IS_DARK_THEME.settingName, value)
        }
    }

    override fun storeUseSystemTheme(value: Boolean) {
        scope.launch {
            settingsStore.storeBooleanSetting(Settings.USE_SYSTEM_THEME.settingName, value)
        }
    }
}

enum class Settings(val settingName: String) {
    SONG_FONT_SIZE("song_font_size"),
    PRO_MODE_IS_ACTIVE("pro_mode_is_active"),
    IS_DARK_THEME("is_dark_theme"),
    USE_SYSTEM_THEME("use_system_theme")
}
