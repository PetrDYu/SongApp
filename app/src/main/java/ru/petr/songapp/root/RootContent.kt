package ru.petr.songapp.root

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.petr.songapp.root.RootComponent.Child.*
import ru.petr.songapp.screens.collections.CollectionsContent
import ru.petr.songapp.screens.songListScreen.SongListScreenContent
import ru.petr.songapp.screens.songScreen.SongScreenContent
import ru.petr.songapp.themeManager.ThemeManagerInstance
import ru.petr.songapp.ui.theme.SongAppTheme

@Composable
fun RootContent(component: RootComponent, modifier: Modifier = Modifier) {
    val childStack by component.childStack.subscribeAsState()
    val isDarkTheme = ThemeManagerInstance.getInstance().isDarkTheme.subscribeAsState()
    val useSystemTheme = component.settings.useSystemTheme.subscribeAsState()
    
    // If using system theme, let SongAppTheme decide automatically,
    // otherwise use our managed theme
    val themeToUse = if (useSystemTheme.value) {
        isSystemInDarkTheme()
    } else {
        isDarkTheme.value
    }

    SongAppTheme(darkTheme = themeToUse) {
        Scaffold(
                modifier,
        ) { paddingValues ->
            Box(Modifier.padding(paddingValues)) {
                Children(
                        stack = childStack,
                        animation = stackAnimation(fade()),
                ) {
                    when (val child = it.instance) {
                        is CollectionsChild -> CollectionsContent(component = child.component,
                                                                modifier = Modifier.fillMaxSize())
                        is SongListScreenChild -> SongListScreenContent(component = child.component,
                                                                        modifier = Modifier.fillMaxSize())
                        is SongChild -> SongScreenContent(component = child.component,
                                                        modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}
