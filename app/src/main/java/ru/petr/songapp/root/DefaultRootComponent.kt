package ru.petr.songapp.root

import android.content.Context
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.Serializable
import ru.petr.songapp.database.room.SongAppDB
import ru.petr.songapp.database.room.songData.SongCollectionDBModel

import ru.petr.songapp.root.RootComponent.Child
import ru.petr.songapp.screens.collections.DefaultCollectionsComponent
import ru.petr.songapp.screens.song.DefaultSongComponent
import ru.petr.songapp.screens.songListScreen.DefaultSongListScreenComponent
import ru.petr.songapp.screens.songListScreen.songList.DefaultSongListComponent

class DefaultRootComponent(
    componentContext: ComponentContext,
    context: Context
) : RootComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    private val rootScope = CoroutineScope(SupervisorJob())

    override val database: SongAppDB = SongAppDB.getDB(context, rootScope)

    private val stack =
        childStack(
                source = navigation,
                serializer = Config.serializer(),
                initialConfiguration = Config.Collections,
                handleBackButton = true,
                childFactory = ::child,
        )

    override val childStack: Value<ChildStack<*, Child>> = stack

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.Collections -> Child.CollectionsChild(DefaultCollectionsComponent(
                    componentContext,
                    database
            ) { id, collections -> navigation.push(Config.SongListScreen(id, collections)) })
            is Config.SongListScreen -> Child.SongListScreenChild(DefaultSongListScreenComponent(
                    componentContext,
                    database,
                    config.collections,
                    config.id,
            ) { collectionId, songId -> navigation.push(Config.Song(collectionId, songId))})
            is Config.Song -> Child.SongChild(DefaultSongComponent(componentContext,
                                                                   database,
                                                                   config.collectionId,
                                                                   config.id))
        }

    private companion object {

    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Collections : Config

        @Serializable
        data class SongListScreen(val id: Int, val collections: List<SongCollectionDBModel>) : Config

        @Serializable
        data class Song(val collectionId: Int, val id: Int) : Config
    }
}