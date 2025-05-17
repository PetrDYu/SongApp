package ru.petr.songapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity

private val DarkColorScheme = darkColorScheme(
    primary = DarkLightBlue,
    secondary = DarkBlue,
    tertiary = DarkLightBlue,
    background = DarkLightBlue,
    onBackground = White,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onTertiaryContainer = DarkLightBlueText,
    onPrimaryContainer = DarkLightGray,
    primaryContainer = DarkLightBlue,
)

private val LightColorScheme = lightColorScheme(
    primary = MainBlue,
    secondary = LightGray,
    tertiary = MainLightBlue,
    background = LightGray,
    onBackground = Black,
    onPrimary = Black,
    onSecondary = Black,
    onTertiary = Black,
    onTertiaryContainer = Color.Gray,
    onPrimaryContainer = MainLightBlue,
    primaryContainer = MainLightBlue,

    /* Other default colors to override
background = Color(0xFFFFFBFE),
surface = Color(0xFFFFFBFE),
onPrimary = Color.White,
onSecondary = Color.White,
onTertiary = Color.White,
onBackground = Color(0xFF1C1B1F),
onSurface = Color(0xFF1C1B1F),
*/
)

@Composable
fun SongAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalView.current.context
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme

            val context = view.context
            if (context is ComponentActivity) {
                context.enableEdgeToEdge()
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}