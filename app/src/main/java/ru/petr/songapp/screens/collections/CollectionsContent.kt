package ru.petr.songapp.screens.collections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState

@Composable
fun CollectionsContent(component: CollectionsComponent, modifier: Modifier = Modifier) {
    val songCollections by component.songCollections.subscribeAsState()

    Column(modifier) {
        val collections = songCollections
        LazyColumn (Modifier.weight(1f)) {
            items(collections.size) {index ->
                val collection = collections[index]
                SongCollectionListItem(
                        name = collection.name,
                        marker = Icons.Default.AddCircle,
                        markerColor = Color(0xFFCF6741)
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
         onClick = onClick
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