package ru.petr.songapp.screens.songScreen.song.models.songParts.linesAndChunks.layers

import ru.petr.songapp.screens.songScreen.song.models.parsing.TagAndAttrNames

enum class ChunkLayerTypes {
    ContinuousDataLayer,
    MarkDataLayer;

    companion object {
        private val continuousDataLayers = listOf(
                TagAndAttrNames.REPEAT_TAG._name,
        )
        private val markDataLayers = listOf(
            TagAndAttrNames.CHORD_TAG._name
        )

        fun getLayerTypeByName(layerTagName: String): ChunkLayerTypes {
            return when(layerTagName) {
                in continuousDataLayers -> {
                    ContinuousDataLayer
                }
                in markDataLayers -> {
                    MarkDataLayer
                }
                else -> {
                    throw IllegalArgumentException("Layer with name $layerTagName doesn't exist")
                }
            }
        }
    }

}