package ru.petr.songapp.screens.songScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.router.slot.child
import kotlinx.coroutines.launch
import ru.petr.songapp.R
import ru.petr.songapp.screens.songScreen.settingsSheet.SettingsSheetContent
import ru.petr.songapp.screens.songScreen.song.SongContent

@Composable
fun SongScreenContent(component: SongScreenComponent,
                      modifier: Modifier = Modifier) {
    ConstraintLayout() {
        val (viewer, settingsButton, editButton) = createRefs()
        SongContent(component = component.song,
                    Modifier.constrainAs(viewer) {
                        top.linkTo(parent.top, margin = 20.dp)
                        bottom.linkTo(parent.bottom, margin = 20.dp)
                        start.linkTo(parent.start, margin = 30.dp)
                        end.linkTo(parent.end, margin = 30.dp)
                    },)
        FloatingActionButton(
            onClick = {
                component.showSettingsSheet()
            },
            Modifier.constrainAs(settingsButton) {
                end.linkTo(parent.end, margin = 30.dp)
                bottom.linkTo(parent.bottom, margin = 30.dp)
            },
        ) {
            Icon(Icons.Default.Settings, stringResource(id = R.string.settings_button_description))
        }
    }
    val settingsSheetSlot by component.settingsSheet.subscribeAsState()
    settingsSheetSlot.child?.instance?.also {
        SettingsSheetContent(component = it)
    }
}