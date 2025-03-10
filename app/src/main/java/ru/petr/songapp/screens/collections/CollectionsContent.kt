package ru.petr.songapp.screens.collections

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.petr.songapp.R

@Composable
fun CollectionsContent(component: CollectionsComponent, modifier: Modifier = Modifier) {
    val collections by component.songCollections.subscribeAsState()
    Column(modifier
               .background(brush =
                           Brush.verticalGradient(
                               colors = listOf(
                                   MaterialTheme.colorScheme.primary,
                                   MaterialTheme.colorScheme.secondary
                               )
                           )
               )
    ) {
        Column (
            Modifier
                .weight(0.7f)
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val titleColor = Color.White
            val logoColor = Color.White
            Text(stringResource(id = R.string.app_title).uppercase(),
                 textAlign = TextAlign.Center,
                 fontSize = 35.sp,
                 modifier = Modifier.padding(bottom = 15.dp),
                 lineHeight = 37.sp,
                 color = titleColor)
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.cherubim_gold),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(logoColor),
            )
        }

        val dbUpdateIsFinished by component.dbUpdateIsFinished.subscribeAsState()
        Crossfade(
            targetState = dbUpdateIsFinished,
            modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
            animationSpec = tween(durationMillis = 1000),
            label = "updateIsFinished"
        ) { updateIsFinished ->
            if (updateIsFinished) {
                LazyColumn(
                    Modifier) {
                    if (collections.isNotEmpty()) {
                        items(collections.size) { index ->
                            val collection = collections[index]
                            SongCollectionListItem(
                                Modifier.padding(bottom = 10.dp),
                                name = collection.name,
                                isStar = index == 0,
                                markerColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ) { component.onSongCollectionClicked(collection.id) }
                        }
                    } else {
                        items(1) {
                            SongCollectionListItem(
                                Modifier.padding(bottom = 10.dp),
                                name = stringResource(R.string.collections_not_added),
                                isStar = false,
                                markerColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ) { }
                        }
                    }
                }
            } else {
                val progress by component.dbUpdatingProgress.subscribeAsState()
                Box(Modifier) {
                    SongCollectionProgressBar(Modifier, progress = { progress })
                }
            }
        }
    }
}

@Composable
fun SongCollectionListItem(modifier: Modifier = Modifier,
                           name: String,
                           isStar: Boolean,
                           markerColor: Color,
                           onClick: ()->Unit
) {
    Card(modifier = modifier.fillMaxSize(),
         onClick = onClick,
         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Row (
            Modifier.padding(vertical = 10.dp, horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            with(LocalDensity.current) {
                if (isStar) {
                    Icon(
                            modifier = Modifier.padding(all = 5.dp).size(25.sp.toDp()),
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = markerColor
                    )
                } else {
                    Surface(
                        modifier = Modifier.padding(all = 5.dp + 5.sp.toDp()).size(15.sp.toDp()),
                        color = markerColor,
                        shape = CircleShape
                    ) {}
                }
            }
            Text(name.uppercase(),
                 Modifier.padding(all = 5.dp),
                 fontSize = 17.sp)
        }
    }
}

@Composable
fun SongCollectionProgressBar(modifier: Modifier = Modifier,
                              progress: () -> Float
) {
    Card(modifier = modifier.fillMaxWidth(),
         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Column (
            Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.updating_is_progress),
                 Modifier.padding(bottom = 15.dp),
                 fontSize = 17.sp,
                 textAlign = TextAlign.Center)
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(),
            )
            Text("${"%.1f".format(progress()*100)}%",
                 fontSize = 17.sp,
                 textAlign = TextAlign.Center)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CollectionsContentPreview() {
    CollectionsContent(component = PreviewCollectionsComponent(0.61f, false))
}