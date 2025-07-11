package ru.petr.songapp.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ru.petr.songapp.commonAndroid.settingsComponent.SettingsComponent
import ru.petr.songapp.screens.collections.DefaultCollectionsComponent
import ru.petr.songapp.screens.songListScreen.DefaultSongListScreenComponent
import ru.petr.songapp.screens.songScreen.DefaultSongScreenComponent

interface RootComponent {
    val childStack: Value<ChildStack<*, Child>>
    val settings: SettingsComponent

    sealed class Child {
        class CollectionsChild(val component: DefaultCollectionsComponent) : Child()
        class SongListScreenChild(val component: DefaultSongListScreenComponent) : Child()
        class SongChild(val component: DefaultSongScreenComponent) : Child()
    }
}
