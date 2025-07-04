package ru.petr.songapp.screens.songScreen

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.coroutines.launch
import ru.petr.songapp.R
import ru.petr.songapp.screens.songScreen.settingsSheet.SettingsSheetContent
import ru.petr.songapp.screens.songScreen.song.SongContent

@Composable
fun SongScreenContent(component: SongScreenComponent,
                      modifier: Modifier = Modifier, ) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var chorusOffset by remember { mutableIntStateOf(0) }
    var chorusHeight by remember { mutableIntStateOf(0) }
    val scrollOnChorus by remember {
        derivedStateOf { scrollState.value - chorusHeight < chorusOffset &&
                         chorusOffset < scrollState.value + chorusHeight / 5 }
    }
    var agreeBackToVerse by remember { mutableStateOf(false) }
    var buttonToChorus by remember(scrollOnChorus) {
        mutableStateOf(!scrollOnChorus || !agreeBackToVerse)
    }

    ConstraintLayout(modifier.background(MaterialTheme.colorScheme.secondary)) {
        val (viewer, settingsButton, nextButton, prevButton, chorusButton) = createRefs()
        SongWrapper(
            component = component,
            modifier = Modifier
                .constrainAs(viewer) {
                    top.linkTo(parent.top, margin = 0.dp)
                    bottom.linkTo(parent.bottom, margin = 0.dp)
                    start.linkTo(parent.start, margin = 30.dp)
                    end.linkTo(parent.end, margin = 30.dp)
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            component.onSongTap()
                        }
                    )
                },
            scrollState = scrollState,
            onChorusOffsetChanged = { offset, height ->
                chorusOffset = offset
                chorusHeight = height
            }
        )

        val chorusQty by remember(component.song.song.value.mSongParts.isEmpty()) {
            mutableIntStateOf(component.song.song.value.getChorusQty())
        }
        val buttonsCount = if (chorusQty == 1) 4 else 3

        val buttonsAreVisible by component.controlsIsVisible.subscribeAsState()

        // Previous button
        val prevButtonIsNeeded by component.prevButtonIsNeeded.subscribeAsState()
        val nextButtonIsNeeded by component.nextButtonIsNeeded.subscribeAsState()

        AnimatedVisibility(
            visible = buttonsAreVisible && prevButtonIsNeeded,
            modifier = Modifier
                .constrainAs(prevButton) {
                    bottom.linkTo(parent.bottom, margin = 14.dp)
                    linkTo(
                        start = parent.start,
                        end = if (buttonsCount == 4) chorusButton.start
                        else settingsButton.start,
                        bias = 0.5f,
                        startMargin = 30.dp,
                        endMargin = 0.dp
                    )
                },
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                onClick = {
                    component.onChangeSongClicked(isNext = false)
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    stringResource(id = R.string.settings_button_description),
                )
            }
        }

        // Chorus button (conditional)
        AnimatedVisibility(
            visible = buttonsAreVisible && chorusQty == 1,
            modifier = Modifier.constrainAs(chorusButton) {
                bottom.linkTo(parent.bottom, margin = 14.dp)
                linkTo(
                    start = if (prevButtonIsNeeded) prevButton.end else parent.start,
                    end = settingsButton.start,
                    bias = 0.5f,
                    startMargin = if (!prevButtonIsNeeded) 30.dp else 0.dp
                )
            },
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            var currentOffset by remember {
                mutableIntStateOf(0)
            }
            FloatingActionButton(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                onClick = {
                    if (buttonToChorus) {
                        currentOffset = scrollState.value
                        scope.launch {
                            scrollState.animateScrollTo(chorusOffset)
                        }
                    } else {
                        scope.launch {
                            scrollState.animateScrollTo(currentOffset)
                        }
                    }
                    buttonToChorus = !buttonToChorus
                    agreeBackToVerse = true
                }
            ) {
                if (buttonToChorus) {
                    Text("П", fontSize = 30.sp)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.KeyboardArrowDown, null)
                        Text(
                            "К",
                            fontSize = 25.sp,
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(includeFontPadding = false)
                            )
                        )
                    }
                }
            }
        }

        // Settings button
        AnimatedVisibility(
            visible = buttonsAreVisible,
            modifier = Modifier.constrainAs(settingsButton) {
                bottom.linkTo(parent.bottom, margin = 14.dp)
                linkTo(
                    start = if (chorusQty == 1) chorusButton.end
                    else if (prevButtonIsNeeded) prevButton.end
                    else parent.start,
                    end = if (nextButtonIsNeeded) nextButton.start else parent.end,
                    bias = 0.5f,
                    startMargin = if (!prevButtonIsNeeded && chorusQty != 1) 30.dp else 0.dp,
                    endMargin = if (!nextButtonIsNeeded) 30.dp else 0.dp
                )
            },
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                onClick = {
                    component.showSettingsSheet()
                }
            ) {
                Icon(
                    Icons.Default.Settings,
                    stringResource(id = R.string.settings_button_description)
                )
            }
        }

        // Next button
        AnimatedVisibility(
            visible = buttonsAreVisible && nextButtonIsNeeded,
            modifier = Modifier.constrainAs(nextButton) {
                bottom.linkTo(parent.bottom, margin = 14.dp)
                linkTo(
                    start = settingsButton.end,
                    end = parent.end,
                    bias = 0.5f,
                    startMargin = 0.dp,
                    endMargin = 30.dp
                )
            },
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                onClick = {
                    component.onChangeSongClicked(isNext = true)
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    stringResource(id = R.string.settings_button_description)
                )
            }
        }
    }

    val settingsSheetSlot by component.settingsSheet.subscribeAsState()
    settingsSheetSlot.child?.instance?.also {
        SettingsSheetContent(component = it)
    }
}

@Composable
fun SongWrapper(modifier: Modifier = Modifier,
                component: SongScreenComponent,
                scrollState: ScrollState = rememberScrollState(),
                onChorusOffsetChanged: (Int, Int) -> Unit,) {
    val songName by component.song.name.subscribeAsState()
    val songNumber by component.song.numberInCollection.subscribeAsState()
    val fontSize by component.song.fontSize.subscribeAsState()
    val isFavorite by component.song.isFavorite.subscribeAsState()
    val headerIsVisible by component.controlsIsVisible.subscribeAsState()
    Column (modifier.fillMaxHeight()) {
        AnimatedVisibility (visible = headerIsVisible) {
            SongScreenHeader(
                Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 10.dp),
                songNumber = songNumber,
                songName = songName,
                fontSize = fontSize,
                isFavorite = isFavorite,
            ) {
                component.setIsFavorite(!isFavorite)
            }
        }
        SongContent(component = component.song,
                    Modifier.verticalScroll(scrollState),
                    onChorusOffsetChanged = onChorusOffsetChanged)

    }
}

@Composable
fun SongScreenHeader(modifier: Modifier = Modifier,
                     songNumber: Int,
                     songName: String,
                     fontSize: Int,
                     isFavorite: Boolean,
                     onFavoriteClick: ()->Unit) {
    Card (
        modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        Row (verticalAlignment = Alignment.CenterVertically) {
            val icon = if (isFavorite) {
                Icons.Outlined.Favorite
            } else {
                Icons.Outlined.FavoriteBorder
            }
            Icon(imageVector = icon,
                 contentDescription = null,
                 modifier = Modifier
                     .clickable { onFavoriteClick() }
                     .padding(10.dp)
                     .size(with(LocalDensity.current) { (fontSize + 2).sp.toDp() }),
                 tint = Color.White
            )
            Box(
                Modifier
                    .padding(vertical = 10.dp)
                    .padding(end = 20.dp)) {
                if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Row {
                        SongNumberForHeader(songNumber = songNumber, withPoint = true, fontSize = fontSize)
                        SongNameForHeader(songName = songName, maxLines = 1, fontSize = fontSize)
                    }
                } else {
                    Column {
                        SongNumberForHeader(songNumber = songNumber, withPoint = false, fontSize = fontSize)
                        SongNameForHeader(songName = songName, maxLines = 2, fontSize = fontSize)
                    }
                }
            }
        }
    }
}

@Composable
fun SongNumberForHeader(songNumber: Int, withPoint: Boolean, fontSize: Int) {
    Text(
        "$songNumber" + if (withPoint) ". " else "",
        fontSize = (fontSize + 2).sp
    )
}

@Composable
fun SongNameForHeader(songName: String, maxLines: Int, fontSize: Int) {
    Text(
        songName.uppercase(),
        fontSize = (fontSize + 2).sp,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        lineHeight = (fontSize + 4).sp
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SongScreenHeaderPreview() {
    SongScreenHeader(songNumber = 33,
                     songName = "Этот день сотворил Господь",
                     fontSize = 20,
                     isFavorite = false,
                     onFavoriteClick = {})
}
