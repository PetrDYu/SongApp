package ru.petr.songapp.screens.songScreen.song.models.songParts.linesAndChunks.layers

import ru.petr.songapp.screens.songScreen.song.models.parsing.TagAndAttrNames

class ChordLayer(
    override val layerChunkId: Int,
    val chord: Chord,
    override val layerId: Int = 0,
) : ChunkLayer.AddingLayer {

    companion object: ChunkLayerCompanion {
        override val layerTagName: String = TagAndAttrNames.CHORD_TAG._name
        override val layerName: String
            get() = TODO("Надо подумать, как динамически получать из ресурсов")
        override val layerType: ChunkLayerTypes = ChunkLayerTypes.MarkDataLayer
    }

    data class Chord(val mainChord: MAIN_CHORDS, val isMinor: Boolean, val sign: SIGNS, val chordType: CHORD_TYPES) {
        override fun toString(): String {
            return "$mainChord${sign.text}${if(isMinor) "m" else ""}${chordType.text}"
        }
    }

    enum class MAIN_CHORDS {
        A, B, C, D, E, F, G, H
    }

    enum class SIGNS(val text: String) {
        NONE(""), DIEZ("#"), BEMOL("b");

        companion object {
            fun fromText(text: String): SIGNS {
                for (sign in SIGNS.values()) {
                    if (text == sign.text) {
                        return sign
                    }
                }
                throw IllegalArgumentException("This sign is unknown ($text)")
            }
        }
    }

    enum class CHORD_TYPES(val text: String){
        NONE(""),
        SEPT("7");
//        DIM_SEPT("dim7"),
//        SEPT_SUS_4("7sus4"),
//        SEXT("6"),

        companion object {
            fun fromText(text: String): CHORD_TYPES {
                for (type in CHORD_TYPES.values()) {
                    if (text == type.text) {
                        return type
                    }
                }
                throw IllegalArgumentException("This chord type is unknown ($text)")
            }
        }
    }
}