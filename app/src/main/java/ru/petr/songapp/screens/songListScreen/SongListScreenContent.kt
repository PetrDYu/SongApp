package ru.petr.songapp.screens.songListScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.pages.ChildPages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.petr.songapp.R
import ru.petr.songapp.screens.common.searchBar.SearchBarContent
import ru.petr.songapp.screens.songListScreen.settingsDialog.SongListSettingsDialogContent
import ru.petr.songapp.screens.songListScreen.songList.SongListContent
import ru.petr.songapp.ui.theme.SongAppTheme
import ru.petr.songapp.themeManager.ThemeManagerInstance

/**
 * Tag used for logging in this file
 */
const val LOG_TAG = "SongListScreenContentTag"

/**
 * Main composable for displaying the song list screen.
 * This screen shows a collection of songs with search functionality and view mode switching.
 *
 * @param component The SongListScreenComponent that manages the screen's state and logic
 * @param modifier Optional modifier for customizing the layout
 */
@Composable
fun SongListScreenContent(component: SongListScreenComponent,
                          modifier: Modifier = Modifier) {
    val selectedPage = component.collectionPages.subscribeAsState().value.selectedIndex
    val isInGridMode = component.isInGridMode.subscribeAsState().value
    val isDarkTheme = ThemeManagerInstance.getInstance().isDarkTheme.subscribeAsState().value
    val useSystemTheme = component.settingsDialogComponent.useSystemTheme.subscribeAsState().value
    
    Scaffold (
        modifier,
        topBar = { SongListScreenTopBar(
            collectionName = component.collections[selectedPage].name,
            isInGridMode = isInGridMode,
            isDarkTheme = isDarkTheme,
            useSystemTheme = useSystemTheme,
            onViewModeClick = component::toggleSongsViewMode,
            onThemeClick = ThemeManagerInstance.getInstance()::toggleTheme,
            onSettingsClick = component.settingsDialogComponent::showDialog
        ) },
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            Column() {
                // Display the selected collection's song list with page navigation
                ChildPages(
                    modifier = Modifier.weight(1f),
                    pages = component.collectionPages,
                    onPageSelected = component::selectCollectionByIndex,
                    scrollAnimation = PagesScrollAnimation.Default,
                ) { _, page ->
                    SongListContent(component = page)
                }
                // Display search bar at the bottom of the screen
                SearchBarContent(component.searchBarComponent)
            }
            
            // Settings dialog
            SongListSettingsDialogContent(component.settingsDialogComponent)
        }
    }
}

/**
 * Top bar composable for the song list screen.
 * Displays the current collection name and a toggle button for switching between list and grid views.
 *
 * @param collectionName Name of the currently selected collection to display in the header
 * @param isInGridMode Boolean indicating whether the view is currently in grid mode
 * @param isDarkTheme Boolean indicating whether dark theme is enabled
 * @param useSystemTheme Boolean indicating whether system theme is used
 * @param onViewModeClick Callback invoked when the view mode toggle button is clicked
 * @param onThemeClick Callback invoked when the theme toggle button is clicked
 * @param onSettingsClick Callback invoked when the settings button is clicked
 */
@Composable
fun SongListScreenTopBar(collectionName: String,
                         isInGridMode: Boolean = false,
                         isDarkTheme: Boolean = false,
                         useSystemTheme: Boolean = false,
                         onViewModeClick: () -> Unit = {},
                         onThemeClick: () -> Unit = {},
                         onSettingsClick: () -> Unit = {}) {
    Box(Modifier
        .background(MaterialTheme.colorScheme.secondary)
        .animateContentSize()
        .fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            with(LocalDensity.current) {
                // Animated view mode toggle button that changes between list and grid icons
                AnimatedContent(
                    targetState = isInGridMode,
                    label = "IconToggle"
                ) { inGridMode ->
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onViewModeClick() }
                            .padding(vertical = 10.dp, horizontal = 20.dp)
                            .size(25.sp.toDp()),
                        painter = painterResource(if (inGridMode)
                            R.drawable.list_icon
                        else
                            R.drawable.grid_icon),
                        contentDescription = if (inGridMode) 
                            "Переключиться в режим списка" 
                        else 
                            "Переключиться в режим сетки",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Newline character to restrict vertical (minimal and maximal) space in the header
            Text("\n",
                fontSize = 22.sp,
                modifier = Modifier.padding(vertical = 10.dp),
                minLines = 2,
                maxLines = 2)

            // Animated collection name display with fade transition
            AnimatedContent(
                modifier = Modifier.weight(1f),
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
                        .padding(vertical = 10.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            
            // Spacer to push theme toggle to the right
            Box(Modifier.weight(0.1f))
            
            // Theme toggle button (visible only when not using system theme)
            if (!useSystemTheme) {
                with(LocalDensity.current) {
                    AnimatedContent(
                        targetState = isDarkTheme,
                        label = "ThemeIconToggle"
                    ) { darkMode ->
                        Icon(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onThemeClick() }
                                .padding(vertical = 10.dp, horizontal = 10.dp)
                                .size(25.sp.toDp()),
                            painter = painterResource(if (darkMode) 
                                R.drawable.ic_light_mode 
                            else 
                                R.drawable.ic_dark_mode),
                            contentDescription = if (darkMode) 
                                "Переключиться на светлую тему" 
                            else 
                                "Переключиться на темную тему",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Settings button
            with(LocalDensity.current) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .clip(CircleShape)
                        .clickable { onSettingsClick() }
                        .padding(vertical = 10.dp, horizontal = 10.dp)
                        .size(25.sp.toDp()),
                    contentDescription = "Настройки",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Preview composable for the SongListScreenTopBar.
 * Shows how the top bar looks with a sample collection name in light theme.
 */
@Preview(showBackground = true, showSystemUi = false)
@Composable
fun SongListScreenTopBarPreview() {
    SongAppTheme(darkTheme = false, dynamicColor = false)
    {
        SongListScreenTopBar(
            collectionName = "Будем петь и славить", 
            isInGridMode = false,
            isDarkTheme = false,
            useSystemTheme = false,
            onViewModeClick = {},
            onThemeClick = {},
            onSettingsClick = {}
        )
    }
}
