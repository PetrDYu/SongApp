package ru.petr.songapp.screens.song.models.songParts

import ru.petr.songapp.screens.song.models.songParts.linesAndChunks.SongPartLine

sealed class SongPart(val lines: List<SongPartLine>) {

    class Verse(lines: List<SongPartLine>, val number: Int): SongPart(lines) {}
    class Chorus(lines: List<SongPartLine>, val afterVerseNumber: Int): SongPart(lines) {}
    class Bridge(lines: List<SongPartLine>, val afterVerseNumber: Int): SongPart(lines) {}

    enum class SongPartTypes() {
        VERSE, CHORUS, BRIDGE
    }
}