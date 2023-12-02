package ru.petr.songapp.screens.songScreen.settingsSheet

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import ru.petr.songapp.commonAndroid.settings

class DefaultSettingsSheetComponent(
    componentContext: ComponentContext,
    private val onDismissBottomSheet: () -> Unit,
) : SettingsSheetComponent, ComponentContext by componentContext {
    override val fontSize: Value<Int> = settings.songFontSize

    override fun onDismissRequest() {
        onDismissBottomSheet()
    }

    override fun onFontSizeChanged(newSize: Int) {
        settings.storeSongFontSize(newSize)
    }
}