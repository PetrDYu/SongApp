package ru.petr.songapp.screens.songScreen.song.models.parsing

import org.xmlpull.v1.XmlPullParser
import ru.petr.songapp.screens.songScreen.song.models.Song
import ru.petr.songapp.screens.songScreen.song.models.songParts.SongPart
import ru.petr.songapp.screens.songScreen.song.models.songParts.linesAndChunks.LineChunk
import ru.petr.songapp.screens.songScreen.song.models.songParts.linesAndChunks.SongPartLine
import ru.petr.songapp.screens.songScreen.song.models.songParts.linesAndChunks.layers.ChunkLayer

object SongPartBuilder {

    fun getPartBuilder(tagName: String?): PartBuilderFunc? {
        var result: PartBuilderFunc? = null
        when (tagName) {
            TagAndAttrNames.VERSE_TAG._name -> {result = SongPartBuilder::buildVerse
            }
            TagAndAttrNames.CHORUS_TAG._name -> {result = SongPartBuilder::buildChorus
            }
            TagAndAttrNames.BRIDGE_TAG._name -> {result = SongPartBuilder::buildBridge
            }
        }
        return result
    }

    private fun buildVerse(layerStack: Song.LayerStack, xmlString: String, attributes: Map<String, String>): SongPart.Verse {
        return parseLines(
                layerStack,
                xmlString,
                attributes.getValue(TagAndAttrNames.NUMBER_ATTR._name).toInt(),
        )
    }

    private fun buildChorus(layerStack: Song.LayerStack, xmlString: String, attributes: Map<String, String>): SongPart.Chorus {
        return parseLines(
                layerStack,
                xmlString,
                attributes.getValue(TagAndAttrNames.NUMBER_ATTR._name).toInt(),
        )
    }

    private fun buildBridge(layerStack: Song.LayerStack, xmlString: String, attributes: Map<String, String>): SongPart.Bridge {
        return parseLines(
                layerStack,
                xmlString,
                attributes.getValue(TagAndAttrNames.NUMBER_ATTR._name).toInt(),
        )
    }

    private inline fun <reified T : SongPart> parseLines(layerStack: Song.LayerStack, xmlString: String, vararg args: Any): T {
        val primaryConstructor = T::class.constructors.first()
        val lines = mutableListOf<SongPartLine>()
        val parser = instantiateNewParser(xmlString.byteInputStream())
        val extractor = FromTagToTagExtractor()
        val layers = CurrentLayersHolder() //mutableListOf<ChunkLayer>()
        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            if (parser.name == TagAndAttrNames.STRING_TAG._name) {
                if (parser.eventType == XmlPullParser.START_TAG) {
                    extractor.setStartPoint(parser.lineNumber, parser.columnNumber)
                } else if (parser.eventType == XmlPullParser.END_TAG) {
                    extractor.setEndPoint(parser.lineNumber, parser.columnNumber - 1)
                    lines.add(
                        LineBuilder.buildLine(
                            extractor.extractPart(xmlString),
                            layers,
                            layerStack,
                            onFindClosedTag = { newLayer ->
                                processClosingTag(newLayer, lines)
                            },
                        )
                    )
                    extractor.clean()
                }
            }
            parser.next()
        }
        return primaryConstructor.call(lines, *args)
    }

    private fun processClosingTag(newLayer: ChunkLayer, lines: MutableList<SongPartLine>) {
        if (lines.isEmpty()){
            return
        }
        for (lineInd in lines.lastIndex downTo 0) {
            val newChunks = mutableListOf<LineChunk>()
            val wasFoundOpeningTag = LineBuilder.processClosingTag(lines[lineInd].chunks, newLayer, newChunks)
            lines[lineInd] = SongPartLine(newChunks)
            if (wasFoundOpeningTag) {
                break
            }
        }
    }

    class CurrentLayersHolder {
        var layers = listOf<ChunkLayer>()
            private set
        var lastOperation = Operations.NONE
        fun add(layer: ChunkLayer) {
            val tempLayers = layers.toMutableList()
            tempLayers.add(layer)
            layers = tempLayers.toList()
            lastOperation = Operations.ADD
        }

        fun removeByIndex(layInd: Int) {
            val tempLayers = layers.toMutableList()
            tempLayers.removeAt(layInd)
            layers = tempLayers.toList()
            lastOperation = Operations.REMOVE
        }

        fun removeIf(filter: (layer: ChunkLayer) -> Boolean) {
            val tempLayers = layers.toMutableList()
            tempLayers.removeIf(filter)
            layers = tempLayers.toList()
            lastOperation = Operations.REMOVE
        }

        enum class Operations {
            ADD,
            REMOVE,
            NONE,
        }
    }
}