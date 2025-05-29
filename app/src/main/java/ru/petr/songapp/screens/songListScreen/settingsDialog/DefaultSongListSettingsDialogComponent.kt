package ru.petr.songapp.screens.songListScreen.settingsDialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import ru.petr.songapp.commonAndroid.settings

class DefaultSongListSettingsDialogComponent(
    componentContext: ComponentContext
) : SongListSettingsDialogComponent, ComponentContext by componentContext {
    
    private val _isVisible = MutableValue(false)
    override val isVisible: Value<Boolean> = _isVisible
    
    override val useSystemTheme: Value<Boolean> = settings.useSystemTheme

    override val isDarkTheme: Value<Boolean> = settings.isDarkTheme

    override fun showDialog() {
        _isVisible.update { true }
    }
    
    override fun hideDialog() {
        _isVisible.update { false }
    }
    
    override fun toggleSystemThemeUse() {
        settings.storeUseSystemTheme(!useSystemTheme.value)
    }

    override fun toggleTheme() {
        settings.storeIsDarkTheme(!isDarkTheme.value)
    }
}
