package ru.petr.songapp.screens.collections

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.petr.songapp.R
import ru.petr.songapp.ui.theme.TitleBlue

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
                .padding(horizontal = 30.dp, vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val titleColor = Color.White
            val logoColor = Color.White
            Text(stringResource(id = R.string.app_title).uppercase(),
                 fontSize = 35.sp,
                 modifier = Modifier.padding(bottom = 20.dp),
                 color = titleColor)
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.cherubim_gold),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(logoColor),
            )
        }
        LazyColumn (
            Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)) {
            items(collections.size) {index ->
                val collection = collections[index]
                SongCollectionListItem(
                    Modifier.padding(bottom = 10.dp),
                    name = collection.name,
                    isStar = index == 0,
                    markerColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) { component.onSongCollectionClicked(collection.id) }
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


@Preview(showBackground = true)
@Composable
fun CollectionsContentPreview() {
    CollectionsContent(component = PreviewCollectionsComponent())
}