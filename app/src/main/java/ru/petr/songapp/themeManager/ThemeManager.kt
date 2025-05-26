package ru.petr.songapp.themeManager

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
}

/**
 * Default implementation of ThemeManager
 * @param stateKeeper StateKeeper for saving theme state across configuration changes
 * @param settings SettingsComponent for accessing settings
 */
class DefaultThemeManager(
    private val stateKeeper: StateKeeper,
    private val settings: SettingsComponent
) : ThemeManager {
    /**
     * Mutable storage for dark theme state with state restoration
     */
    private val _isDarkTheme = MutableValue(stateKeeper.consume("is_app_dark_theme", Boolean.serializer()) ?: settings.isDarkTheme.value)
    
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

        settings.isDarkTheme.subscribe { isDarkTheme ->
            _isDarkTheme.update { isDarkTheme }
        }

    }
    
    /**
     * Toggles between dark and light theme
     */
    override fun toggleTheme() {
        settings.storeIsDarkTheme(!_isDarkTheme.value)
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
     */
    fun initialize(stateKeeper: StateKeeper, settings: SettingsComponent) {
        if (instance == null) {
            instance = DefaultThemeManager(stateKeeper, settings)
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