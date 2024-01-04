package ru.petr.songapp.screens.songScreen.song.models.parsing

import org.xmlpull.v1.XmlPullParser
import ru.petr.songapp.database.room.songData.SongDBModel
import ru.petr.songapp.screens.songScreen.song.models.Song
import ru.petr.songapp.screens.songScreen.song.models.parsing.SongPartBuilder.getPartBuilder
import ru.petr.songapp.screens.songScreen.song.models.songParts.SongPart

object SongBuilder {

    fun getSong(
        song: SongDBModel,
        //parentCollection: SongCollectionDBModel
    ): Song {
        //val numberInCollection = SongNumberInCollection(song.songData.numberInCollection, parentCollection)
        val songParts = mutableListOf<SongPart>()
        val layerStack = Song.LayerStack()

        val parser = instantiateNewParser(song.body.byteInputStream())
        var attributes: Map<String, String> = mapOf()
        val extractor = FromTagToTagExtractor()
        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            getPartBuilder(parser.name)?.let { partBuilder ->
                if (parser.eventType == XmlPullParser.START_TAG) {
                    extractor.setStartPoint(parser.lineNumber, parser.columnNumber)
                    attributes = fetchAttributes(parser)
                } else if (parser.eventType == XmlPullParser.END_TAG) {
                    extractor.setEndPoint(parser.lineNumber, parser.columnNumber - 1)
                    songParts.add(
                        partBuilder(
                            layerStack,
                            extractor.extractPart(song.body),
                            attributes
                        )
                    )
                    extractor.clean()
                }
            }
            parser.next()
        }

        return Song(songParts, song.songData.isFixed, layerStack)
    }
}