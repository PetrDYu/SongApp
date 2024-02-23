package ru.petr.songapp.screens.songScreen.settingsSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import ru.petr.songapp.R

@Composable
fun SettingsSheetContent(component: SettingsSheetComponent,
                         modifier: Modifier = Modifier) {
    ModalBottomSheet(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondary,
        onDismissRequest = {
            component.onDismissRequest()
        },
        //sheetState = sheetState
    ) {
        val maxFontSize = integerResource(id = R.integer.max_font_size)
        val minFontSize = integerResource(id = R.integer.min_font_size)
        val curFontSize by component.fontSize.subscribeAsState()

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            SongFontSizeSettingField(curFontSize, minFontSize, maxFontSize) { newSize ->
                component.onFontSizeChanged(newSize)
            }
        }
    }
}

@Composable
fun SongFontSizeSettingField(fontSize: Int, minFontSize: Int, maxFontSize: Int, onFontSizeChange: (newSize: Int) -> Unit) {
    var expanded by remember{ mutableStateOf(false) }
    Column() {
        Text(
            modifier = Modifier.padding(top = 5.dp),
            text = stringResource(id = R.string.font_size_setting_label),
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {expanded = !expanded}) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .clickable { expanded = true }
                    .menuAnchor(),
                value = fontSize.toString(),
                onValueChange = {},
                singleLine = true,
                readOnly = true,
                enabled = false,
                textStyle = TextStyle(fontSize = fontSize.sp),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    disabledTextColor = MaterialTheme.colorScheme.onSecondary,
                    disabledContainerColor = MaterialTheme.colorScheme.secondary
                )
            )
            ExposedDropdownMenu(expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.secondary)) {
                for (curFontSize in minFontSize..maxFontSize) {
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            onFontSizeChange(curFontSize)
                        },
                        text = {
                            Text(text = curFontSize.toString(), fontSize = curFontSize.sp)
                        },
                    )
                    if (curFontSize != maxFontSize)
                        Divider(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp, horizontal = 10.dp))
                }
            }
        }
    }
}