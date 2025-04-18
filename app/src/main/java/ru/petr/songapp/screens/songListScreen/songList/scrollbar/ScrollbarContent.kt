package ru.petr.songapp.screens.songListScreen.songList.scrollbar

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.coroutines.delay
import ru.petr.songapp.screens.songListScreen.LOG_TAG
import kotlin.math.roundToInt

val DEFAULT_SCROLLBAR_TEXT_SIZE = 14.sp

@Composable
fun ScrollbarContent(component: ScrollbarComponent,
                     modifier: Modifier,
) {
    val isBright by component.isBright.subscribeAsState()
    val currentSongNumber by component.currentSongNumber.subscribeAsState()

    val scrollbarAlpha: Float by animateFloatAsState(
        targetValue = if (isBright) 1f else 0.5f,
        animationSpec = tween(200)
    )

    val offset by component.scrollOffset.subscribeAsState()

    var scrollbarPointerHeight by remember { mutableFloatStateOf(0f) }

    // Получаем текущее значение конфигурации
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    // Каждый раз, когда configuration.fontScale изменяется, срабатывает этот блок
    LaunchedEffect(configuration.fontScale) {
        with(density) {
            component.setTextSizeInPx(DEFAULT_SCROLLBAR_TEXT_SIZE.toPx())
        }
    }

    val isVisible by component.isVisible.subscribeAsState()
    AnimatedVisibility(
        isVisible,
        modifier = modifier,
        enter = slideIn(
            initialOffset = { fullSize -> IntOffset(fullSize.width, 0) },
            animationSpec = tween(durationMillis = 700)
        ),
        exit = slideOut(
            targetOffset = { fullSize -> IntOffset(fullSize.width, 0) },
            animationSpec = tween(durationMillis = 700)
        ),
    ) {
        // Оборачиваем содержимое в Box, чтобы наложить трек с номерами на скроллбар
        Box {
            // Белая подложка для трека с номерами
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight()
                    .background(Color.White.copy(alpha = scrollbarAlpha))
                    .onSizeChanged { size ->
                        component.setColumnHeight(size.height.toFloat())
                    }
            ) {
                val numbersList by component.numbersList.subscribeAsState()
                val numbersPositions by component.numbersPositions.subscribeAsState()
                numbersList.forEachIndexed { index, number ->
                    Text(
                        text = number.toString(),
                        modifier = Modifier
                            .offset { IntOffset(x = 0, y = numbersPositions[index].toInt()) }
                            .align(Alignment.TopEnd),
                        fontSize = DEFAULT_SCROLLBAR_TEXT_SIZE,
                        color = Color(
                            red = 0,
                            green = 73,
                            blue = 101,
                            alpha = 255
                        ).copy(alpha = scrollbarAlpha),
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        lineHeight = DEFAULT_SCROLLBAR_TEXT_SIZE
                    )
                }
            }

            // Непосредственно указатель скроллбара
            ScrollbarPointer(
                // Добавляем измерение размера указателя
                modifier.onSizeChanged { size ->
                    val newHeight = size.height.toFloat()
                    if (newHeight != scrollbarPointerHeight) {
                        component.setPointerHeight(newHeight)
                    }
                },
                offset = offset,
                scrollbarAlpha = scrollbarAlpha,
                onPress = { component.onPress() },
                onReleaseOrCancel = { component.onReleaseOrCancel() },
                currentSongNumber = currentSongNumber,
                onDrag = { dragDelta ->
                    component.onDrag(dragDelta)
                },
                isBright = isBright
            )
        }
    }
}

@Composable
fun ScrollbarPointer(modifier: Modifier = Modifier,
                     offset: Float,
                     scrollbarAlpha: Float,
                     onPress: () -> Unit,
                     onReleaseOrCancel: () -> Unit,
                     onDrag: (offset: Float) -> Unit,
                     currentSongNumber: Int,
                     isBright: Boolean) {
    var dragActive by remember { mutableStateOf(false) }

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        with (LocalDensity.current) {
            // Подсказка в форме капли
            AnimatedVisibility(
                modifier = Modifier
                    .offset { IntOffset(x = 15.dp.toPx().toInt(), y = offset.roundToInt()) },
                visible = isBright && currentSongNumber > 0,
                enter = fadeIn(animationSpec = tween(150)),
                exit = fadeOut(animationSpec = tween(150))
            ) {
                Box {
                    // Форма капли
                    Canvas(modifier = Modifier.size(100.dp, 40.dp)) {
                        val width = size.width
                        val height = size.height

                        val path = Path().apply {
                            moveTo(width * 1f, height * 0.5f)
                            lineTo(width * 0.66f, 0f)
                            lineTo(0f, 0f)
                            lineTo(0f, height)
                            lineTo(width * 0.66f, height)
                            lineTo(width * 1f, height * 0.5f)
                            close()
                        }

                        // Цвет заполнения
                        drawPath(
                            path = path,
                            color = Color(red = 0, green = 73, blue = 101, alpha = 255),
                        )
                    }

                    // Номер песни внутри капли
                    Text(
                        text = currentSongNumber.toString(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = (-5).dp) // Небольшой сдвиг влево, чтобы текст был по центру капли
                    )
                }
            }
        }

        Box(
            Modifier
                .fillMaxHeight(0.1f)
                .width(30.dp)
                .offset { IntOffset(x = 0, y = offset.roundToInt()) }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            onPress()
                            val released = tryAwaitRelease()
                            if (released) {
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
                    detectDragGestures(
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
                    .background(
                        color = Color(
                            red = 0,
                            green = 73,
                            blue = 101,
                            alpha = 255
                        ).copy(alpha = scrollbarAlpha),
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {}


        }

    }
}