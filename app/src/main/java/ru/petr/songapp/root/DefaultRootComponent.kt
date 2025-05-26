package ru.petr.songapp.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import ru.petr.songapp.commonAndroid.databaseComponent.SongCollection
import ru.petr.songapp.commonAndroid.settings

import ru.petr.songapp.root.RootComponent.Child
import ru.petr.songapp.screens.collections.DefaultCollectionsComponent
import ru.petr.songapp.screens.songListScreen.DefaultSongListScreenComponent
import ru.petr.songapp.screens.songScreen.DefaultSongScreenComponent
import ru.petr.songapp.themeManager.ThemeManagerInstance

class DefaultRootComponent(
    componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {
    init {
        // Initialize theme manager
        ThemeManagerInstance.initialize(stateKeeper, settings)
    }
    
    private val navigation = StackNavigation<Config>()

    private val stack =
        childStack(
                source = navigation,
                serializer = Config.serializer(),
                initialConfiguration = Config.Collections,
                handleBackButton = true,
                childFactory = ::child,
        )

    override val childStack: Value<ChildStack<*, Child>> = stack

    @OptIn(DelicateDecomposeApi::class)
    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.Collections -> Child.CollectionsChild(DefaultCollectionsComponent(
                    componentContext,
            ) { id, collections -> navigation.push(Config.SongListScreen(id, collections)) })
            is Config.SongListScreen -> Child.SongListScreenChild(
                DefaultSongListScreenComponent(
                    componentContext,
                    config.collections,
                    config.id,
                ) { collectionId, songId ->
                    navigation.push(Config.Song(collectionId, songId))
                })
            is Config.Song -> Child.SongChild(
                DefaultSongScreenComponent(
                    componentContext,
                    config.collectionId,
                    config.id,
                    onChangeSongBtnClicked = { collectionId, songId ->
                        navigation.pop()
                        navigation.push(
                            Config.Song(
                                collectionId,
                                songId
                            )
                        )},
                ))
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Collections : Config

        @Serializable
        data class SongListScreen(val id: Int, val collections: List<SongCollection>) : Config

        @Serializable
        data class Song(val collectionId: Int, val id: Int) : Config
    }
}