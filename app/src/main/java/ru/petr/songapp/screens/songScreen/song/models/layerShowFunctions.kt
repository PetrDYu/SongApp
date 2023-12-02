package ru.petr.songapp.screens.songScreen.song.models

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import ru.petr.songapp.screens.songScreen.song.models.songParts.linesAndChunks.layers.ChordLayer
import ru.petr.songapp.screens.songScreen.song.models.songParts.linesAndChunks.layers.ChunkLayer

@Composable
fun ShowAddingLayer(layer: ChunkLayer.AddingLayer, fontSize: Int) {
    when (layer) {
        is ChordLayer -> {
            ShowChordLayer(layer, fontSize = fontSize)
        }
        else -> {
            throw IllegalArgumentException("Incorrect adding layer class ${layer::class.simpleName}")
        }
    }
}

@Composable
fun ShowEmptyAddingLayer(layer: ChunkLayer.AddingLayer, fontSize: Int) {
    when (layer) {
        is ChordLayer -> {
            ShowChordLayer(layer, true, fontSize)
        }
        else -> {
            throw IllegalArgumentException("Incorrect adding layer class ${layer::class.simpleName}")
        }
    }
}

@Composable
fun ShowChordLayer(layer: ChordLayer, isEmpty: Boolean = false, fontSize: Int) {
    val text = if (isEmpty) " " else layer.chord.toString()
    Text(text, fontStyle = FontStyle.Italic, fontSize = (fontSize - 2).sp )
}