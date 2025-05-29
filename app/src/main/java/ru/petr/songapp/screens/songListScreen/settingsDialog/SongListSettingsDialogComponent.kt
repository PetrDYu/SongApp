package ru.petr.songapp.screens.songListScreen.settingsDialog

import com.arkivanov.decompose.value.Value

interface SongListSettingsDialogComponent {
    /**
     * Whether the settings dialog is visible
     */
    val isVisible: Value<Boolean>
    
    /**
     * Whether to use system theme
     */
    val useSystemTheme: Value<Boolean>
    
    /**
     * Flag indicating whether dark theme is currently enabled
     */
    val isDarkTheme: Value<Boolean>
    
    /**
     * Shows the settings dialog
     */
    fun showDialog()
    
    /**
     * Hides the settings dialog
     */
    fun hideDialog()
    
    /**
     * Toggles system theme usage
     */
    fun toggleSystemThemeUse()
    
    /**
     * Тoggles the active theme between light and dark modes when system theme is disabled.
     * This method only takes effect if "useSystemTheme" is set to false, allowing manual
     * theme preference override. Does not affect the system theme usage setting itself.
     */
    fun toggleTheme()
}