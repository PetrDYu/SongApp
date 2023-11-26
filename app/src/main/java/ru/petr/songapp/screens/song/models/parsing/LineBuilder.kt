package ru.petr.songapp.screens.song.models.parsing

import org.xmlpull.v1.XmlPullParser
import ru.petr.songapp.screens.song.models.Song
import ru.petr.songapp.screens.song.models.songParts.linesAndChunks.LineChunk
import ru.petr.songapp.screens.song.models.songParts.linesAndChunks.SongPartLine
import ru.petr.songapp.screens.song.models.songParts.linesAndChunks.layers.ChunkLayer
import ru.petr.songapp.screens.song.models.songParts.linesAndChunks.layers.ChunkText

object LineBuilder {

    fun buildLine(xmlString: String,
                  currentLayers: SongPartBuilder.CurrentLayersHolder,//MutableList<ChunkLayer>,
                  layerStack: Song.LayerStack,
                  onFindClosedTag: (newLayer: ChunkLayer) -> Unit,
    ): SongPartLine {
        var chunks = mutableListOf<LineChunk>()

        val parser = instantiateNewParser(xmlString.byteInputStream())
        var chunk = LineChunk()
        var isPlainTagNow = false
        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                if (parser.name == TagAndAttrNames.PLAIN_TAG._name) {
                    isPlainTagNow = true
                } else if (parser.name != TagAndAttrNames.STRING_TAG._name) {
                    chunk.setLayersIfNotSet(currentLayers.layers/*toList()*/)
                    addChunkToChunks(chunk, currentLayers, chunks)
                    chunk = LineChunk()
                    val attributes = fetchAttributes(parser).toMutableMap()
                    ChunkLayersBuilder.modifyLayerList(
                        layerStack,
                        currentLayers,
                        parser.name,
                        attributes,
                        onFindMarkDataTag = { newLayer ->
                            processMarkDataTag(chunk, currentLayers, newLayer)
                        },
                        onFindClosedTag = { newLayer ->
                            val localLayerList = currentLayers.layers.toMutableList()
                            val layerIndex = localLayerList.indexOfFirst { layer -> layer.isSameWithLayer(newLayer) }
                            localLayerList[layerIndex] = newLayer
//                            val result = chunk.setLayersIfNotSet(localLayerList.toList())
//                            if (!result) {
//                                throw IllegalStateException("Chunk already has layers list")
//                            }
                            val newChunks = mutableListOf<LineChunk>()
                            val wasFoundOpeningTag = processClosingTag(chunks, newLayer, newChunks)
                            chunks = newChunks
                            if (!wasFoundOpeningTag) {
                                onFindClosedTag(newLayer)
                            }
                        },
                    )
//                    chunk.setLayersIfNotSet(currentLayers.layers/*toList()*/)
//                    addChunkToChunks(chunk, currentLayers, chunks)
//                    chunk = LineChunk()
                }
            } else if (parser.eventType == XmlPullParser.TEXT) {
                if (isPlainTagNow) {
                    chunk.text = ChunkText(parser.text)
                }
            } else if (parser.eventType == XmlPullParser.END_TAG) {
                if (parser.name == TagAndAttrNames.PLAIN_TAG._name) {
                    isPlainTagNow = false
                }
            }
            parser.next()
        }
        addChunkToChunks(chunk, currentLayers, chunks)
        return SongPartLine(chunks)
    }

    private fun addChunkToChunks(chunk: LineChunk,
                                 currentLayers: SongPartBuilder.CurrentLayersHolder,//MutableList<ChunkLayer>,
                                 chunks: MutableList<LineChunk>) {
        chunk.setLayersIfNotSet(currentLayers.layers/*toList()*/)
        if (chunk.text == null) {
            chunk.text = ChunkText("")
        }
        if (chunks.isEmpty()) {
            if (!chunk.isEmpty()) {
                chunks.add(chunk)
            }
        } else {
            val previousChunk = chunks[chunks.lastIndex]
            if (previousChunk.text!!.text == "") {
                val newChunk = unionChunks(previousChunk, chunk)
                chunks.removeAt(chunks.lastIndex)
                chunks.add(newChunk)
            } else {
                chunks.add(chunk)
            }
        }
    }

    // Здесь предполагается,что chunk не содержит MarkDataLayer'а
    private fun processPreviousChunkWithTextNull(previousChunk: LineChunk,
                                                 previousPosition: Int,
                                                 chunk: LineChunk,
                                                 chunks: MutableList<LineChunk>,
                                                 currentLayers: SongPartBuilder.CurrentLayersHolder
    ) {
        if (previousChunk.text == null) {
            if (currentLayers.lastOperation == SongPartBuilder.CurrentLayersHolder.Operations.ADD) {
                chunks.removeAt(chunks.lastIndex - (previousPosition - 1))
                chunks.add(chunk)
            } else if (currentLayers.lastOperation == SongPartBuilder.CurrentLayersHolder.Operations.REMOVE) {
                if (chunk.text != null) {
                    chunks.add(chunk)
                }
            } else {
                throw IllegalStateException("Unknown operation ${currentLayers.lastOperation.name}")
            }
        } else {
            if (currentLayers.lastOperation != SongPartBuilder.CurrentLayersHolder.Operations.REMOVE) {
                chunks.add(chunk)
            } else {
                if (chunk.text != null) {
                    chunks.add(chunk)
                }
            }
        }
    }

    private fun unionChunks(chunk1: LineChunk, chunk2: LineChunk): LineChunk {
        val newLayers = chunk1.layers.toMutableList()
        chunk2.layers.forEach { layer ->
            if (!layersHasLayer(layer, newLayers)) {
                newLayers.add(layer)
            }
        }
        val newChunk = LineChunk()
        newChunk.setLayersIfNotSet(newLayers.toList())
        newChunk.text = ChunkText("${chunk1.text!!.text}${chunk2.text!!.text}")
        return newChunk
    }

    private fun layersHasLayer(layer: ChunkLayer, layers: List<ChunkLayer>): Boolean {
        return null != layers.find { it.isSameWithLayer(layer) }
    }

    private fun chunkHasNullTextAndSameLayersWithPreviousChunk(chunk: LineChunk,
                                                               chunks: List<LineChunk>): Boolean {
        return (chunk.text == null &&
                chunks.isNotEmpty() &&
                chunk.hasSameLayers(chunks[chunks.lastIndex]))
    }

    fun processClosingTag(chunks: List<LineChunk>, newLayer: ChunkLayer, newChunks: MutableList<LineChunk>): Boolean {
        for (chunk in chunks) {
            newChunks.add(chunk)
        }
        var wasFoundOpeningTag = true
        if (newChunks.isNotEmpty()) {
            var chunkInd = newChunks.lastIndex
            var curChunk = newChunks[chunkInd]
            while (curChunk.hasSameLayer(newLayer)) {
                val layers = curChunk.layers.toMutableList()
                val sameLayerInd = layers.indexOfFirst { chunkLayer -> chunkLayer.isSameWithLayer(newLayer) }
                layers[sameLayerInd] = newLayer
                newChunks[chunkInd] = curChunk.copy(layers = layers.toList())
                chunkInd--
                if (chunkInd < 0) {
                    wasFoundOpeningTag = false
                    break
                }
                curChunk = newChunks[chunkInd]
            }
        }
        return wasFoundOpeningTag
    }

    private fun processMarkDataTag(chunk: LineChunk, currentLayers: SongPartBuilder.CurrentLayersHolder, newLayer: ChunkLayer) {
        currentLayers.add(newLayer)
        val result = chunk.setLayersIfNotSet(currentLayers.layers)
        if (!result) {
            throw IllegalStateException("Chunk already has layers list")
        }
        currentLayers.removeByIndex(currentLayers.layers.lastIndex)
    }
}