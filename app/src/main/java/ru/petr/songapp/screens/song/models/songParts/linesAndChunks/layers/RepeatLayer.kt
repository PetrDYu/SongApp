package ru.petr.songapp.screens.song.models.songParts.linesAndChunks.layers

import android.content.Context
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import ru.petr.songapp.R
import ru.petr.songapp.screens.song.models.parsing.TagAndAttrNames

class RepeatLayer(override val layerChunkId: Int,
                  val repRate: Int,
                  override val layerId: Int=0,
): ChunkLayer.WrappingLayer {

    companion object: ChunkLayerCompanion {
        // Tag name according this layer
        override val layerTagName: String = TagAndAttrNames.REPEAT_TAG._name
        // Layer name to show on screen in layer stack
        override val layerName: String
            get() = TODO("Not yet implemented")
        override val layerType: ChunkLayerTypes = ChunkLayerTypes.getLayerTypeByName(layerTagName)
    }

    override fun modifyTextAndStyle(text: ChunkText,
                                    style: TextStyle,
                                    isStart: Boolean,
                                    isEnd: Boolean,
                                    isMultiline: Boolean,
                                    context: Context,
    ): Pair<ChunkText, TextStyle> {
        var textResult: ChunkText = text
        if (!isMultiline) {
            if (isEnd) {
                textResult = ChunkText(text.text + context.resources.getString(R.string.times, repRate))
            }
        }

        var styleResult: TextStyle = style
        if (!isMultiline) {
            if (styleResult.textDecoration == TextDecoration.Underline) {
                styleResult = styleResult.copy(fontStyle = FontStyle.Italic)
            } else {
                styleResult = styleResult.copy(textDecoration = TextDecoration.Underline)
            }
        }
        return textResult to styleResult
    }
}