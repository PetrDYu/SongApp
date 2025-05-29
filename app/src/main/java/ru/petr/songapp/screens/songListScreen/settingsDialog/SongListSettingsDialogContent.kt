package ru.petr.songapp.screens.songListScreen.settingsDialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import ru.petr.songapp.R
import ru.petr.songapp.ui.theme.SongAppTheme

@Composable
fun SongListSettingsDialogContent(
    component: SongListSettingsDialogComponent,
    modifier: Modifier = Modifier
) {
    val isVisible: Boolean by component.isVisible.subscribeAsState()
    val useSystemTheme by component.useSystemTheme.subscribeAsState()
    val isDarkTheme by component.isDarkTheme.subscribeAsState()
    
    if (isVisible) {
        Dialog(
            onDismissRequest = component::hideDialog
        ) {
            Card(
                modifier = modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Настройки",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Использовать системную тему",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Switch(
                            checked = useSystemTheme,
                            onCheckedChange = { component.toggleSystemThemeUse() }
                        )
                    }

                    if (!useSystemTheme) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Выбрать тему",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            with(LocalDensity.current)
                            {
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
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = component::hideDialog
                        ) {
                            Text("Закрыть")
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
    override val useSystemTheme = MutableValue(false)
    override val isDarkTheme: Value<Boolean> = MutableValue(false)

    override fun showDialog() {}
    override fun hideDialog() {}
    override fun toggleSystemThemeUse() {}
    override fun toggleTheme() {}
}

@Preview(showBackground = true)
@Composable
fun SongListSettingsDialogContentPreview() {
    SongAppTheme {
        SongListSettingsDialogContent(
            component = PreviewSongListSettingsDialogComponent()
        )
    }
}
