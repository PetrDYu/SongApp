package ru.petr.songapp.screens.songListScreen

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.pages.ChildPages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.petr.songapp.screens.common.searchBar.SearchBarContent
import ru.petr.songapp.screens.songListScreen.songList.SongListContent
import kotlin.math.roundToInt

const val LOG_TAG = "SongListScreenContentTag"

@Composable
fun SongListScreenContent(component: SongListScreenComponent,
                          modifier: Modifier = Modifier) {
    val selectedPage = component.collectionPages.subscribeAsState().value.selectedIndex
    Scaffold (
        modifier,
        topBar = { SongListScreenTopBar(collectionName = component.collections[selectedPage].name) },
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            Column() {
                ChildPages(
                    modifier = Modifier.weight(1f),
                    pages = component.collectionPages,
                    onPageSelected = component::selectCollectionByIndex,
                    scrollAnimation = PagesScrollAnimation.Default,
                ) { _, page ->
                    SongListContent(component = page)
                }
                SearchBarContent(component.searchBarComponent)
            }
            var yOffset by remember { mutableFloatStateOf(0f) }
            var draggableState = rememberDraggableState { distance ->
                val newYOffset = yOffset + distance
                if (newYOffset >= 0f)
                    yOffset = newYOffset
            }
            var xdraggableState = rememberDraggableState { distance ->
            }
            var isBright by remember { mutableStateOf(false) }
            var dragActive by remember { mutableStateOf(false) }
            val scrollbarAlpha: Float by animateFloatAsState(
                targetValue = if (isBright) 1f else 0.5f,
                animationSpec = tween(200)
            )
            Box (
                Modifier
                    .fillMaxWidth(0.1f)
                    .fillMaxHeight(0.1f)
                    .align (Alignment.TopEnd)
                    .offset { IntOffset(x = 0, y = yOffset.roundToInt()) }
                    .background(color = MaterialTheme.colorScheme.primary.copy(alpha = scrollbarAlpha))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isBright = true
                                val released = tryAwaitRelease()
                                if (released)
                                {
                                    isBright = false
                                    Log.d(LOG_TAG, "Scrollbar released")
                                } else {
                                    if (!dragActive) {
                                        isBright = false
                                    }
                                    Log.d(LOG_TAG, "Scrollbar press cancelled")
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures (
                            onDragStart = {
                                dragActive = true
                            },
                            onDragEnd = {
                                isBright = false
                                dragActive = false
                            },
                            onDragCancel = {
                                isBright = false
                                dragActive = false
                            },
                            onDrag = { _, offset ->
                                val newYOffset = yOffset + offset.y
                                if (newYOffset >= 0f)
                                    yOffset = newYOffset
                            }
                        )
                    }
//                    .draggable(
//                        orientation = Orientation.Horizontal,
//                        state = xdraggableState,
//                        onDragStarted = {
//                            Log.d(LOG_TAG, "Scrollbar horizontal drag started")
//                        },
//                        onDragStopped = {
//                            Log.d(LOG_TAG, "Scrollbar horizontal drag stopped")
//                        })
//                    .draggable(
//                        orientation = Orientation.Vertical,
//                        state = draggableState,
//                        onDragStarted = {
//                            dragActive = true
//                        },
//                        onDragStopped = {
//                            isBright = false
//                            dragActive = false
//                            Log.d(LOG_TAG, "Scrollbar drag stopped")
//                        }
//                    )
            ) {}
        }
    }
}

@Composable
fun SongListScreenTopBar(collectionName: String) {
    Box(Modifier
            .background(MaterialTheme.colorScheme.secondary)
            .animateContentSize()
            .fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth(0.7f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            with(LocalDensity.current) {
                Icon(
                    modifier = Modifier
                        .clickable { }
                        .padding(vertical = 10.dp, horizontal = 20.dp)
                        .size(25.sp.toDp()),
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            AnimatedContent(
                targetState = collectionName.uppercase(),
                transitionSpec = {
                    fadeIn(animationSpec = tween(durationMillis = 500)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 500))
                },
                contentAlignment = Alignment.Center,
                label = "animatedHeaderText"
            ) { animatedText ->
                Text(
                    animatedText,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                )
            }

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SongListScreenTopBarPreview() {
    SongListScreenTopBar("Будем петь и славить")
}