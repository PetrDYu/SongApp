package ru.petr.songapp.screens.songListScreen.songList.scrollbar

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder
import kotlinx.coroutines.delay
import ru.petr.songapp.ui.theme.DarkScrollbarColor
import ru.petr.songapp.ui.theme.DarkScrollbarPointerColor
import ru.petr.songapp.ui.theme.DarkScrollbarTextColor
import ru.petr.songapp.ui.theme.DarkScrollbarTooltipColor
import ru.petr.songapp.ui.theme.LightScrollbarColor
import ru.petr.songapp.ui.theme.LightScrollbarPointerColor
import ru.petr.songapp.ui.theme.LightScrollbarTextColor
import ru.petr.songapp.ui.theme.LightScrollbarTooltipColor
import ru.petr.songapp.ui.theme.SongAppTheme
import ru.petr.songapp.themeManager.ThemeManagerInstance
import kotlin.math.roundToInt

// Tag for logging
const val LOG_TAG = "Scrollbar"
// Default text size for scrollbar numbers
val DEFAULT_SCROLLBAR_TEXT_SIZE = 14.sp
// Default text size for the tooltip showing current song number
val DEFAULT_SCROLLBAR_TOOLTIP_TEXT_SIZE = 18.sp

/**
 * Main composable for the customizable scrollbar
 * Shows a vertical scrollbar with song numbers and a draggable pointer
 *
 * @param component The component providing business logic and state for the scrollbar
 * @param modifier Modifier for customizing the scrollbar appearance
 */
@Composable
fun ScrollbarContent(component: ScrollbarComponent,
                     modifier: Modifier,
) {
    val isBright by component.isBright.subscribeAsState()
    val currentSongNumber by component.currentSongNumber.subscribeAsState()

    val isDarkTheme = ThemeManagerInstance.getInstance().isDarkTheme.subscribeAsState().value
    val scrollbarColor = if (isDarkTheme) DarkScrollbarColor else LightScrollbarColor
    val scrollbarTextColor = if (isDarkTheme) DarkScrollbarTextColor else LightScrollbarTextColor

    // Animate scrollbar opacity based on its active state
    val scrollbarAlpha: Float by animateFloatAsState(
        targetValue = if (isBright) 1f else 0.5f,
        animationSpec = tween(200)
    )

    val offset by component.scrollOffset.subscribeAsState()

    var scrollbarPointerHeight by remember { mutableFloatStateOf(0f) }

    // Get current configuration to handle font scale changes
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    // Update text size when font scale changes
    LaunchedEffect(configuration.fontScale) {
        with(density) {
            component.setTextSizeInPx(DEFAULT_SCROLLBAR_TEXT_SIZE.toPx())
        }
    }

    var scrollbarTouchWidth by remember { mutableStateOf(0.dp) }
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
        // Wrap content in Box to overlay number track on scrollbar
        Box {
            // White background for the numbers track
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight()
                    .background(scrollbarColor.copy(alpha = scrollbarAlpha))
                    .onSizeChanged { size ->
                        component.setColumnHeight(size.height.toFloat())
                        scrollbarTouchWidth = with(density) {size.width.toDp()}
                    }
            ) {
                val numbersList by component.numbersList.subscribeAsState()
                val numbersPositions by component.numbersPositions.subscribeAsState()
                // Display selected song numbers at their calculated positions
                numbersList.forEachIndexed { index, number ->
                    Text(
                        text = number.toString(),
                        modifier = Modifier
                            .offset { IntOffset(x = 0, y = numbersPositions[index].toInt()) }
                            .align(Alignment.TopEnd)
                            .padding(horizontal = 5.dp),
                        fontSize = DEFAULT_SCROLLBAR_TEXT_SIZE,
                        color = scrollbarTextColor,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        lineHeight = DEFAULT_SCROLLBAR_TEXT_SIZE
                    )
                }
            }

            // Scrollbar pointer/indicator that can be dragged
            ScrollbarPointer(
                // Measure pointer size to update component state
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
                isBright = isBright,
                scrollbarTouchWidth = scrollbarTouchWidth,
                scrollbarPointerColor = if (isDarkTheme) DarkScrollbarPointerColor else LightScrollbarPointerColor,
                scrollbarTooltipColor = if (isDarkTheme) DarkScrollbarTooltipColor else LightScrollbarTooltipColor
            )
        }
    }
}

/**
 * Composable for the scrollbar pointer/indicator
 * Shows a draggable pointer with a tooltip displaying the current song number
 *
 * @param modifier Modifier for pointer customization
 * @param offset Vertical position of the pointer
 * @param scrollbarAlpha Opacity of the scrollbar
 * @param onPress Callback for when pointer is pressed
 * @param onReleaseOrCancel Callback for when pointer is released or drag is canceled
 * @param onDrag Callback for when pointer is dragged
 * @param currentSongNumber Current song number to display in tooltip
 * @param isBright Whether the pointer is in active/bright state
 * @param scrollbarTouchWidth Width of the interactive area
 * @param scrollbarPointerColor Color of the pointer element
 * @param scrollbarTooltipColor Color of the tooltip background
 */
@Composable
fun ScrollbarPointer(modifier: Modifier = Modifier,
                     offset: Float,
                     scrollbarAlpha: Float,
                     onPress: () -> Unit,
                     onReleaseOrCancel: () -> Unit,
                     onDrag: (offset: Float) -> Unit,
                     currentSongNumber: Int,
                     isBright: Boolean,
                     scrollbarTouchWidth: Dp,
                     scrollbarPointerColor: Color,
                     scrollbarTooltipColor: Color) {

    var dragActive by remember { mutableStateOf(false) }

    // Configure tooltip appearance and behavior
    val tooltipBuilder = rememberBalloonBuilder {
        setArrowSize(10)
        setArrowPosition(0.5f)
        setArrowOrientation(ArrowOrientation.END)
        setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
        setWidth(BalloonSizeSpec.WRAP)
        setHeight(BalloonSizeSpec.WRAP)
        setPadding(12)
        setCornerRadius(8f)
        setBackgroundColor(scrollbarTooltipColor.toArgb())
        setBalloonAnimation(BalloonAnimation.CIRCULAR)
    }

    Box(
        modifier
            .fillMaxHeight(0.1f)
            .width(scrollbarTouchWidth)
            .offset { IntOffset(x = 0, y = offset.roundToInt()) }
            .pointerInput(Unit) {
                // Handle tap gestures on the pointer
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
                // Handle drag gestures on the pointer
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
        Balloon(
            modifier = Modifier.align(Alignment.TopEnd),
            builder = tooltipBuilder,
            balloonContent = {
                // Tooltip content showing current song number
                Box(
                    Modifier
                        .width( with(LocalDensity.current) { (5 * DEFAULT_SCROLLBAR_TOOLTIP_TEXT_SIZE.toPx()).toDp() })
                ) {
                    Text(
                        text ="$currentSongNumber",
                        modifier = Modifier.align(Alignment.CenterStart),
                        color = Color.White,
                        fontSize = DEFAULT_SCROLLBAR_TOOLTIP_TEXT_SIZE,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        ) { balloonWindow ->

            // Reshow the tooltip if it had started closing (e.g. user released pointer)
            // but then the user touched the scrollbar pointer again (making isBright true).
            balloonWindow.setOnBalloonDismissListener {
                if (isBright) {
                    balloonWindow.showAlignStart()
                }
            }

            var tooltipIsShowed by remember { mutableStateOf(false) }

            // Update tooltip position when the pointer offset changes
            LaunchedEffect(offset) {
                // If tooltip is already shown, update its position
                if (tooltipIsShowed) {
                    balloonWindow.updateAlignStart()
                }
            }

            // Show/hide tooltip based on the active state
            LaunchedEffect(isBright) {
                if (isBright) {
                    tooltipIsShowed = true
                    balloonWindow.showAlignStart()
                } else {
                    tooltipIsShowed = false
                    balloonWindow.dismiss()
                }
            }

            // Visual representation of the scrollbar pointer
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .background(
                        color = scrollbarPointerColor.copy(alpha = scrollbarAlpha),
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {}
        }


    }
}

/**
 * Preview of the scrollbar component for design time visualization
 */
@Preview(showBackground = true, backgroundColor = 0xFF021F2F,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES or android.content.res.Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PreviewScrollbar() {
    // Для превью используем локальное значение темы
    SongAppTheme(darkTheme = true, dynamicColor = false) {
        val component = PreviewScrollbarComponent(
            isBright = MutableValue(true),
            scrollOffset = MutableValue(0f),
            isVisible = MutableValue(true),
            itemsQty = MutableValue(100),
            numbersList = MutableValue(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
            numbersPositions = MutableValue(
                listOf(
                    100f,
                    200f,
                    300f,
                    400f,
                    500f,
                    600f,
                    700f,
                    800f,
                    900f,
                    1000f
                )
            ),
            pointerHeight = MutableValue(20f),
            currentSongNumber = MutableValue(1)
        )
        ScrollbarContent(component, Modifier)
    }
}