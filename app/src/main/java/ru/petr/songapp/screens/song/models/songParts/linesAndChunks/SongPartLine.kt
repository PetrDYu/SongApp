package ru.petr.songapp.screens.song.models.songParts.linesAndChunks

class SongPartLine (val chunks: List<LineChunk>) {

    fun getChunksSplitByWords(): List<LineChunk> {
        val resultChunks: MutableList<LineChunk> = mutableListOf()
        chunks.forEach { chunk ->
            resultChunks.addAll(chunk.splitByWords())
        }
        return resultChunks
    }
}