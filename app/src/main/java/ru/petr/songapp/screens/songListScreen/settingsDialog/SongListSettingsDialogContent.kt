package ru.petr.songapp.screens.songListScreen.settingsDialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.delay
import ru.petr.songapp.R
import ru.petr.songapp.ui.theme.SongAppTheme

@Composable
fun SongListSettingsDialogContent(
    component: SongListSettingsDialogComponent,
    modifier: Modifier = Modifier
) {
    val isDialogVisible: Boolean by component.isVisible.subscribeAsState()
    val useSystemTheme by component.useSystemTheme.subscribeAsState()
    val isDarkTheme by component.isDarkTheme.subscribeAsState()

    var showThemeSelectionRow by remember { mutableStateOf(!useSystemTheme) }
    val dialogHeightAnimationDuration = 700
    val rowAnimationDuration = 300

    val targetHeight = if (useSystemTheme) 170.dp else 250.dp
    val animatedHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "dialogHeight"
    )

    LaunchedEffect(useSystemTheme) {
        if (!useSystemTheme) {
            delay(dialogHeightAnimationDuration.toLong() / 2)
            showThemeSelectionRow = true
        } else {
            showThemeSelectionRow = false
        }
    }

    if (isDialogVisible) {
        Dialog(
            onDismissRequest = component::hideDialog
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .height(animatedHeight) // Применяем анимированную высоту к Card
            ) {
                // Этот Column теперь будет занимать всю высоту Card
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = "Настройки",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Использовать системную тему",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Switch(
                                checked = useSystemTheme,
                                onCheckedChange = {
                                    component.toggleSystemThemeUse()
                                    if (it) {
                                        showThemeSelectionRow = false
                                    }
                                }
                            )
                        }

                        AnimatedVisibility(
                            visible = showThemeSelectionRow && !useSystemTheme,
                            enter = fadeIn(animationSpec = tween(rowAnimationDuration))/* + expandVertically(
                                animationSpec = tween(rowAnimationDuration)
                            )*/,
                            exit = fadeOut(animationSpec = tween(rowAnimationDuration))/* + shrinkVertically(
                                animationSpec = tween(rowAnimationDuration)
                            )*/
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 50.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Выбрать тему",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                with(LocalDensity.current) {
                                    Icon(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .padding(vertical = 10.dp, horizontal = 10.dp)
                                            .size(25.sp.toDp()),
                                        painter = painterResource(R.drawable.ic_light_mode),
                                        contentDescription = "Переключиться на светлую тему",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Switch(
                                        checked = isDarkTheme,
                                        onCheckedChange = { component.toggleTheme() }
                                    )
                                    Icon(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .padding(vertical = 10.dp, horizontal = 10.dp)
                                            .size(25.sp.toDp()),
                                        painter = painterResource(R.drawable.ic_dark_mode),
                                        contentDescription = "Переключиться на темную тему",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    } // Конец Column с weight(1f)

                    // Кнопка "Закрыть" всегда внизу
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = component::hideDialog
                        ) {
                            Text(
                                text = "Закрыть",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}


// Preview component for the settings dialog
private class PreviewSongListSettingsDialogComponent : SongListSettingsDialogComponent {
    override val isVisible = MutableValue(true)
    private val _useSystemTheme = MutableValue(true) // Start with system theme for preview
    override val useSystemTheme: Value<Boolean> = _useSystemTheme
    override val isDarkTheme: Value<Boolean> = MutableValue(false)


    override fun showDialog() {}
    override fun hideDialog() {}
    override fun toggleSystemThemeUse() {
        _useSystemTheme.value = !_useSystemTheme.value
    }
    override fun toggleTheme() {}
}

@Preview(showBackground = true, name = "Dialog - System Theme")
@Composable
fun SongListSettingsDialogContentPreviewSystem() {
    SongAppTheme {
        SongListSettingsDialogContent(
            component = PreviewSongListSettingsDialogComponent()
        )
    }
}

@Preview(showBackground = true, name = "Dialog - Manual Theme")
@Composable
fun SongListSettingsDialogContentPreviewManual() {
    val component = remember {
        PreviewSongListSettingsDialogComponent().apply {
            // Directly set initial state for this preview
            (useSystemTheme as MutableValue).value = false
        }
    }
    SongAppTheme {
        SongListSettingsDialogContent(
            component = component
        )
    }
}