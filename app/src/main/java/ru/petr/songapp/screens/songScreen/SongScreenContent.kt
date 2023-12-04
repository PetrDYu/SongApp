package ru.petr.songapp.screens.songScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import ru.petr.songapp.R
import ru.petr.songapp.screens.songScreen.settingsSheet.SettingsSheetContent
import ru.petr.songapp.screens.songScreen.song.SongContent

@Composable
fun SongScreenContent(component: SongScreenComponent,
                      modifier: Modifier = Modifier) {
    ConstraintLayout(modifier.background(colorResource(id = R.color.main_white))) {
        val (header, viewer, settingsButton, editButton) = createRefs()
        SongWrapper(component = component,
                    modifier = Modifier.constrainAs(viewer) {
                        top.linkTo(parent.top, margin = 0.dp)
                        bottom.linkTo(parent.bottom, margin = 0.dp)
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
//            containerColor = colorResource(id = R.color.main_blue_light),
//            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
        ) {
            Icon(Icons.Default.Settings, stringResource(id = R.string.settings_button_description))
        }
    }
    val settingsSheetSlot by component.settingsSheet.subscribeAsState()
    settingsSheetSlot.child?.instance?.also {
        SettingsSheetContent(component = it)
    }
}

@Composable
fun SongWrapper(modifier: Modifier = Modifier,
                component: SongScreenComponent) {
    val songName by component.song.name.subscribeAsState()
    val songNumber by component.song.numberInCollection.subscribeAsState()
    val fontSize by component.song.fontSize.subscribeAsState()
    Column (modifier) {
        SongScreenHeader(
            Modifier
                .padding(horizontal = 10.dp)
                .padding(top = 10.dp),
            songNumber = songNumber,
            songName = songName,
            fontSize = fontSize
        ) {

        }
        SongContent(component = component.song,
                    Modifier)

    }
}

@Composable
fun SongScreenHeader(modifier: Modifier = Modifier,
                     songNumber: Int,
                     songName: String,
                     fontSize: Int,
                     onFavoriteClick: ()->Unit) {
    Card (
        modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.main_blue_light))
    ) {
        Row (verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Outlined.Star,
                 contentDescription = null,
                 modifier = Modifier
                     .clickable { onFavoriteClick() }
                     .padding(10.dp)
                     .size((fontSize + 2).dp))
            Column (
                Modifier
                    .padding(vertical = 10.dp)
                    .padding(end = 20.dp)) {
                Text("$songNumber", fontSize = (fontSize + 2).sp)
                Text(songName.uppercase(), fontSize = (fontSize + 2).sp)
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SongScreenHeaderPreview() {
    SongScreenHeader(songNumber = 33,
                     songName = "Этот день сотворил Господь",
                     fontSize = 20,
                     onFavoriteClick = {})
}
