package ru.petr.songapp.screens.songScreen

import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.serialization.Serializable
import ru.petr.songapp.commonAndroid.databaseComponent
import ru.petr.songapp.screens.songScreen.settingsSheet.DefaultSettingsSheetComponent
import ru.petr.songapp.screens.songScreen.settingsSheet.SettingsSheetComponent
import ru.petr.songapp.screens.songScreen.song.DefaultSongComponent
import ru.petr.songapp.screens.songScreen.song.SongComponent

class DefaultSongScreenComponent(
    componentContext: ComponentContext,
    private val collectionId: Int,
    songId: Int,
    private val onChangeSongBtnClicked: (collectionId: Int, songId: Int) -> Unit,
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

    private var prevSongId = songId
    private var nextSongId = songId

    private var prevSongIdCancel: Cancellation? = null
    private var nextSongIdCancel: Cancellation? = null

    private val _prevButtonIsNeeded = MutableValue(false)
    override val prevButtonIsNeeded: Value<Boolean> = _prevButtonIsNeeded
    private val _nextButtonIsNeeded = MutableValue(false)
    override val nextButtonIsNeeded:  Value<Boolean> = _nextButtonIsNeeded

    private val _controlsIsVisible = MutableValue(true)
    override val controlsIsVisible: Value<Boolean> = _controlsIsVisible


    init {
        song.numberInCollection.subscribe { curSongNum ->
            prevSongIdCancel?.cancel()
            nextSongIdCancel?.cancel()
            prevSongIdCancel = databaseComponent.getSongIdByNumAndCollection(curSongNum - 1, collectionId).subscribe { songId ->
                prevSongId = songId
                _prevButtonIsNeeded.update { prevSongId != -1 }
            }
            nextSongIdCancel = databaseComponent.getSongIdByNumAndCollection(curSongNum + 1, collectionId).subscribe { songId ->
                nextSongId = songId
                _nextButtonIsNeeded.update { nextSongId != -1 }
            }
        }
    }

    override fun showSettingsSheet() {
        bottomSheetNavigation.activate(BottomSheetConfig)
    }

    override fun setIsFavorite(isFavorite: Boolean) {
        databaseComponent.updateSongIsFavorite(song.songId, isFavorite)
    }

    override fun onChangeSongClicked(isNext: Boolean) {
        onChangeSongBtnClicked(
            collectionId, if (isNext) nextSongId else prevSongId
        )
    }

    override fun onSongTap() {
        _controlsIsVisible.update { isVisible -> !isVisible }
    }

    @Serializable // kotlinx-serialization plugin must be applied
    private object BottomSheetConfig
}