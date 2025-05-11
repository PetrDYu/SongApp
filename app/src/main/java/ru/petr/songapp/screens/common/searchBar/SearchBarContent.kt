package ru.petr.songapp.screens.common.searchBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.petr.songapp.R

/**
 * Top-level composable for the search bar component.
 * Subscribes to the component state and delegates to SearchSongBar for UI rendering.
 *
 * @param component The SearchBarComponent that manages search state and logic
 * @param modifier Optional modifier for customizing the layout
 */
@Composable
fun SearchBarContent(component: SearchBarComponent,
                     modifier: Modifier = Modifier) {
    val text: String by component.searchText.subscribeAsState()
    SearchSongBar(modifier,
                  searchText = text,
                  onChangeSearchText = component::onChangeSearchText,
                  onSearchButtonClick = component::onSearch)
}

/**
 * Search bar UI implementation that provides a text input field with search and clear buttons.
 * Handles keyboard actions and focus management for improved user experience.
 *
 * @param modifier Optional modifier for customizing the layout
 * @param searchText Current text in the search field
 * @param onChangeSearchText Callback to update the search text
 * @param onSearchButtonClick Callback to initiate a search operation
 */
@Composable
fun SearchSongBar(modifier: Modifier = Modifier,
                  searchText: String,
                  onChangeSearchText: (newText: String)->Unit,
                  onSearchButtonClick: ()->Unit) {
    Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.background(MaterialTheme.colorScheme.secondary)
    ) {
        // Access keyboard controller to hide keyboard after search
        val keyboardController = LocalSoftwareKeyboardController.current
        // Focus manager to clear focus after search
        val focusManager = LocalFocusManager.current
        
        // Search text input field with clear button
        OutlinedTextField(
                value = searchText,
                onValueChange = onChangeSearchText,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                        onDone = {
                            onSearchButtonClick()
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                ),
                placeholder = { Text(text = stringResource(id = R.string.search_bar_placeholder)) },
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .weight(1f),
                singleLine = true,
                trailingIcon = {
                    // Clear button to empty the search field
                    IconButton(onClick = {onChangeSearchText("")}) {
                        Icon(Icons.Default.Clear, 
                             contentDescription = "Clear search text", 
                             Modifier.size(24.dp))
                    }
                }
        )
        //        var offset by remember { mutableStateOf(0f) }
        //        var wordoffset by remember {
        //            mutableStateOf(0f to 0f)
        //        }
        //        Column() {
        //            Text("offset: $offset, word: ${wordoffset.first}: ${wordoffset.second}")
        //            BasicTextField(value = "long1 long2 long3 long4 long5 long6 long7 long8 long9 long10 long11 long12 text",
        //                onValueChange = {},
        //                singleLine = true,
        //                onTextLayout = {layoutResult ->
        //                    val boundary = layoutResult.getWordBoundary(0)
        //                    val path = layoutResult.getPathForRange(boundary.start, boundary.end)
        //                    wordoffset = path.getBounds().left to path.getBounds().right
        //                },
        //                modifier = Modifier
        //                    .fillMaxWidth()
        //                    .scrollable(
        //                        orientation = Orientation.Horizontal,
        //                        state = rememberScrollableState { delta ->
        //                            offset += delta
        //                            delta
        //                        }
        //                    )
        //            )
        //        }

        // Search button to initiate search
        IconButton(
                onClick = {
                    onSearchButtonClick()
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
        ) {
            Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    Modifier
                        .size(35.dp)
                        .padding(end = 6.dp),
            )
        }
    }
}