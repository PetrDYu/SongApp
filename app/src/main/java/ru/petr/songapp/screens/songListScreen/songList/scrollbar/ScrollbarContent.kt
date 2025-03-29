package ru.petr.songapp.screens.songListScreen.songList.scrollbar

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.coroutines.delay
import ru.petr.songapp.screens.songListScreen.LOG_TAG
import kotlin.math.roundToInt

@Composable
fun ScrollbarContent(component: ScrollbarComponent,
                     modifier: Modifier) {
    val isBright by component.isBright.subscribeAsState()

    val scrollbarAlpha: Float by animateFloatAsState(
        targetValue = if (isBright) 1f else 0.5f,
        animationSpec = tween(200)
    )

    val offset by component.scrollOffset.subscribeAsState()

    var scrollbarPointerHeight by remember { mutableFloatStateOf(0f) }

    ScrollbarPointer(
        modifier.onGloballyPositioned { layoutCoordinates ->
            val newHeight = layoutCoordinates.size.height.toFloat()
            if (newHeight != scrollbarPointerHeight) {
                component.setPointerHeight(newHeight)
            }

        },
        offset = offset,
        scrollbarAlpha = scrollbarAlpha,
        onPress = {
            component.onPress()
        },
        onReleaseOrCancel = {
            component.onReleaseOrCancel()
        },
        onDrag = { offset ->
            component.onDrag(offset)
        }
    )
}

@Composable
fun ScrollbarPointer(modifier: Modifier = Modifier,
                     offset: Float,
                     scrollbarAlpha: Float,
                     onPress: () -> Unit,
                     onReleaseOrCancel: () -> Unit,
                     onDrag: (offset: Float) -> Unit,) {
    var dragActive by remember { mutableStateOf(false) }

    Box (
        modifier
            .fillMaxHeight(0.1f)
            .width(30.dp)
            .offset { IntOffset(x = 0, y = offset.roundToInt()) }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPress()
                        val released = tryAwaitRelease()
                        if (released)
                        {
                            onReleaseOrCancel()
                            Log.d(LOG_TAG, "Scrollbar released")
                        } else {
                            delay(100)
                            if (!dragActive) {
                                onReleaseOrCancel()
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
                        onPress()
                    },
                    onDragEnd = {
                        onReleaseOrCancel()
                        dragActive = false
                    },
                    onDragCancel = {
                        onReleaseOrCancel()
                        dragActive = false
                    },
                    onDrag = { _, offset ->
                        onDrag(offset.y)
                    }
                )
            }
    ) {

        Box(
            Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .background(color = Color(red = 0, green = 73, blue = 101, alpha = 255).copy(alpha = scrollbarAlpha))){}
    }
}