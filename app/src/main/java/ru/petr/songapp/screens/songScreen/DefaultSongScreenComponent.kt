package ru.petr.songapp.screens.songScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import ru.petr.songapp.commonAndroid.databaseComponent
import ru.petr.songapp.screens.songScreen.settingsSheet.DefaultSettingsSheetComponent
import ru.petr.songapp.screens.songScreen.settingsSheet.SettingsSheetComponent
import ru.petr.songapp.screens.songScreen.song.DefaultSongComponent
import ru.petr.songapp.screens.songScreen.song.SongComponent

class DefaultSongScreenComponent(
    componentContext: ComponentContext,
    collectionId: Int,
    songId: Int,
) : SongScreenComponent, ComponentContext by componentContext {

    override val song: SongComponent = DefaultSongComponent(childContext("DefaultSongComponent"),
                                                            collectionId,
                                                            songId)
    private val bottomSheetNavigation = SlotNavigation<BottomSheetConfig>()

    override val settingsSheet: Value<ChildSlot<*, SettingsSheetComponent>> =
        childSlot(
            source = bottomSheetNavigation,
            serializer = BottomSheetConfig.serializer(),
            handleBackButton = true,
        ) { config, childComponentContext ->
            DefaultSettingsSheetComponent(
                componentContext = childComponentContext,
            ) {
                bottomSheetNavigation.dismiss {  }
            }
        }

    override fun showSettingsSheet() {
        bottomSheetNavigation.activate(BottomSheetConfig)
    }

    override fun setIsFavorite(isFavorite: Boolean) {
        databaseComponent.updateSongIsFavorite(song.songId, isFavorite)
    }

    @Serializable // kotlinx-serialization plugin must be applied
    private object BottomSheetConfig
}