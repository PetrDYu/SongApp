package ru.petr.songapp.themeManager

import android.content.Context
import android.content.res.Configuration
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.serialization.builtins.serializer
import com.arkivanov.essenty.statekeeper.StateKeeper
import ru.petr.songapp.commonAndroid.settingsComponent.SettingsComponent

/**
 * Interface for theme management functionality
 */
interface ThemeManager {
    /**
     * Flag indicating whether dark theme is enabled
     */
    val isDarkTheme: Value<Boolean>

    /**
     * Toggles between dark and light theme
     */
    fun toggleTheme()
    
    /**
     * Updates system theme state (should be called when system theme changes)
     */
    fun updateSystemTheme(isSystemDark: Boolean)
}

/**
 * Default implementation of ThemeManager
 * @param stateKeeper StateKeeper for saving theme state across configuration changes
 * @param settings SettingsComponent for accessing settings
 * @param context Android Context for getting system theme
 */
class DefaultThemeManager(
    private val stateKeeper: StateKeeper,
    private val settings: SettingsComponent,
    private val context: Context
) : ThemeManager {
    /**
     * Mutable storage for dark theme state with state restoration
     */
    private val _isDarkTheme = MutableValue(calculateInitialTheme())
    
    /**
     * Read-only access to dark theme state
     */
    override val isDarkTheme: Value<Boolean> = _isDarkTheme
    
    init {
        // Register for state saving of dark theme
        stateKeeper.register(
            key = "is_app_dark_theme",
            strategy = Boolean.serializer()
        ) { _isDarkTheme.value }

        // Subscribe to settings changes
        settings.isDarkTheme.subscribe { isDarkTheme ->
            updateTheme()
        }
        
        settings.useSystemTheme.subscribe { useSystemTheme ->
            updateTheme()
        }
    }
    
    /**
     * Calculate initial theme based on settings
     */
    private fun calculateInitialTheme(): Boolean {
        val savedTheme = stateKeeper.consume("is_app_dark_theme", Boolean.serializer())
        return savedTheme
            ?: if (settings.useSystemTheme.value) {
                getSystemTheme()
            } else {
                settings.isDarkTheme.value
            }
    }
    
    /**
     * Get current system theme
     */
    private fun getSystemTheme(): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
    
    /**
     * Update theme based on current settings
     */
    private fun updateTheme() {
        val newTheme = if (settings.useSystemTheme.value) {
            getSystemTheme()
        } else {
            settings.isDarkTheme.value
        }
        _isDarkTheme.update { newTheme }
    }
    
    /**
     * Toggles between dark and light theme (only when not using system theme)
     */
    override fun toggleTheme() {
        if (!settings.useSystemTheme.value) {
            settings.storeIsDarkTheme(!_isDarkTheme.value)
        }
    }
    
    /**
     * Updates system theme state (should be called when system theme changes)
     */
    override fun updateSystemTheme(isSystemDark: Boolean) {
        if (settings.useSystemTheme.value) {
            _isDarkTheme.update { isSystemDark }
        }
    }
}

/**
 * Singleton instance of ThemeManager for global access
 */
object ThemeManagerInstance {
    private var instance: ThemeManager? = null
    
    /**
     * Initialize the ThemeManager with a StateKeeper
     * @param stateKeeper StateKeeper for saving theme state
     * @param settings SettingsComponent for accessing settings
     * @param context Android Context for getting system theme
     */
    fun initialize(stateKeeper: StateKeeper, settings: SettingsComponent, context: Context) {
        if (instance == null) {
            instance = DefaultThemeManager(stateKeeper, settings, context)
        }
    }
    
    /**
     * Get the ThemeManager instance
     * @return ThemeManager instance
     * @throws IllegalStateException if ThemeManager has not been initialized
     */
    fun getInstance(): ThemeManager {
        return instance ?: throw IllegalStateException("ThemeManager has not been initialized")
    }
}
