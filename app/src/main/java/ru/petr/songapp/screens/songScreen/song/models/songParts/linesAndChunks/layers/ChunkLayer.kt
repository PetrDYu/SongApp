package ru.petr.songapp.screens.songScreen.song.models.songParts.linesAndChunks.layers

import android.content.Context
import androidx.compose.ui.text.TextStyle
import kotlin.reflect.full.companionObjectInstance

sealed interface ChunkLayer {
    val layerChunkId: Int
    val layerId: Int

    val layerType: ChunkLayerTypes
        get() = (this::class.companionObjectInstance as ChunkLayerCompanion).layerType

    sealed interface WrappingLayer: ChunkLayer {
        fun modifyTextAndStyle(text: ChunkText,
                               style: TextStyle,
                               isStart: Boolean,
                               isEnd: Boolean,
                               isMultiline: Boolean,
                               context: Context,
        ): Pair<ChunkText, TextStyle>

    }
    sealed interface AddingLayer: ChunkLayer

    private fun hasSameLayerTagName(tagName: String): Boolean {
        return (this::class.companionObjectInstance as ChunkLayerCompanion).layerTagName == tagName
    }

    fun isSameWithTagNameLayerChunkIdAndLayerId(tagName: String, layerChunkId: Int, layerId: Int): Boolean {
        return (hasSameLayerTagName(tagName) &&
                (this.layerChunkId == layerChunkId) &&
                (this.layerId == layerId))
    }

    fun isSameWithLayer(layer: ChunkLayer): Boolean {
        return this.isSameWithTagNameLayerChunkIdAndLayerId(
                (layer::class.companionObjectInstance as ChunkLayerCompanion).layerTagName,
                layer.layerChunkId,
                layer.layerId
        )
    }

    fun isSimilarWithLayer(layer: ChunkLayer): Boolean {
        return (hasSameLayerTagName((layer::class.companionObjectInstance as ChunkLayerCompanion).layerTagName) &&
                (this.layerId == layer.layerId))
    }
}

interface ChunkLayerCompanion {
    val layerTagName: String
    val layerName: String
    val layerType: ChunkLayerTypes
}