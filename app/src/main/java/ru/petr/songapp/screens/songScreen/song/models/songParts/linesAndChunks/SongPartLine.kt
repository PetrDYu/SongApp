package ru.petr.songapp.screens.songScreen.song.models.songParts.linesAndChunks

import ru.petr.songapp.screens.songScreen.song.models.songParts.linesAndChunks.layers.ChunkLayer
import ru.petr.songapp.screens.songScreen.song.models.songParts.linesAndChunks.layers.RepeatLayer

class SongPartLine (val chunks: List<LineChunk>) {

    fun getChunksSplitByWords(): List<LineChunk> {
        val resultChunks: MutableList<LineChunk> = mutableListOf()
        chunks.forEach { chunk ->
            resultChunks.addAll(chunk.splitByWords())
        }
        return resultChunks
    }

    fun hasSameRepeatLayerWithLineAndGetQty(line: SongPartLine): Int {
        chunks.forEach { chunk ->
            var repeatLayers = chunk.getRepeatLayers()
            if (repeatLayers.isNotEmpty()) {
                var mainRepeatLayer = repeatLayers.first()
                var mainRepeatLayerId = mainRepeatLayer.layerId
                repeatLayers.forEach {
                    if (it.layerId < mainRepeatLayerId) {
                        mainRepeatLayer = it
                        mainRepeatLayerId = it.layerId
                    }
                }
                if (line.hasSameRepeatLayer(mainRepeatLayer)) {
                    return (mainRepeatLayer as RepeatLayer).repRate
                }
            }
        }
        return 0
    }

    private fun hasSameRepeatLayer(layer: ChunkLayer): Boolean {
        chunks.forEach { chunk ->
            var repeatLayers = chunk.getRepeatLayers()
            if (repeatLayers.isNotEmpty()) {
                repeatLayers.forEach {
                    if (it.isSameWithLayer(layer)) {
                        return true
                    }
                }
            }
        }
        return false
    }
}