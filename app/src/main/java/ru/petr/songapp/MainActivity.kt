package ru.petr.songapp

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import ru.petr.songapp.root.DefaultRootComponent
import ru.petr.songapp.root.RootContent
import ru.petr.songapp.themeManager.ThemeManagerInstance

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val root =
            DefaultRootComponent(
                componentContext = defaultComponentContext(),
                context = this
            )

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                RootContent(component = root, modifier = Modifier.fillMaxSize())
            }
        }
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        
        // Notify theme manager about system theme changes
        val isSystemDark = (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        try {
            ThemeManagerInstance.getInstance().updateSystemTheme(isSystemDark)
        } catch (e: IllegalStateException) {
            // ThemeManager not initialized yet, ignore
        }
    }
}
