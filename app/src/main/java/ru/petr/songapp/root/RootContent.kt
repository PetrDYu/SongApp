package ru.petr.songapp.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import ru.petr.songapp.root.RootComponent.Child.*
import ru.petr.songapp.screens.collections.CollectionsContent
import ru.petr.songapp.screens.songListScreen.SongListScreenContent
import ru.petr.songapp.screens.songScreen.SongScreenContent

@Composable
fun RootContent(component: RootComponent, modifier: Modifier = Modifier) {
    val childStack by component.childStack.subscribeAsState()
    val activeComponent = childStack.active.instance

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