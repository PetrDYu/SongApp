package ru.petr.songapp.screens.collections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import ru.petr.songapp.R

@Composable
fun CollectionsContent(component: CollectionsComponent, modifier: Modifier = Modifier) {
    val collections by component.songCollections.subscribeAsState()

    Column(modifier
               .background(brush =
                           Brush.verticalGradient(
                               colors = listOf(colorResource(id = R.color.main_blue),
                                               Color(0x215E744B)
                               )
                           )
               )
    ) {
        Column (
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 50.dp)) {
            Text(stringResource(id = R.string.app_title), fontSize = 30.sp)
            Icon(modifier = Modifier.size(50.dp),
                 imageVector = Icons.Default.Home,
                 contentDescription = null,
                 tint = Color.White)
        }
        LazyColumn (
            Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)) {
            items(collections.size) {index ->
                val collection = collections[index]
                val marker: ImageVector = if (index == 0) {
                    Icons.Default.Star
                } else {
                    Icons.Default.AddCircle
                }
                SongCollectionListItem(
                    Modifier.padding(bottom = 10.dp),
                    name = collection.name,
                    marker = marker,
                    markerColor = colorResource(id = R.color.main_blue)
                ) { component.onSongCollectionClicked(collection.id) }
            }
        }
    }
}

@Composable
fun SongCollectionListItem(modifier: Modifier = Modifier,
                           name: String,
                           marker: ImageVector,
                           markerColor: Color,
                           onClick: ()->Unit
) {
    Card(modifier = modifier.fillMaxSize(),
         onClick = onClick,
         colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.main_white))
    ) {
        Row (Modifier.padding(vertical = 10.dp, horizontal = 5.dp)) {
            Icon(
                    modifier = Modifier.padding(all = 5.dp),
                    imageVector = marker,
                    contentDescription = null,
                    tint = markerColor
            )
            Text(name.uppercase(), Modifier.padding(all = 5.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CollectionsContentPreview() {
    CollectionsContent(component = PreviewCollectionsComponent())
}