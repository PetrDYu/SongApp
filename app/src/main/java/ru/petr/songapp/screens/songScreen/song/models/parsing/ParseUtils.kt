package ru.petr.songapp.screens.songScreen.song.models.parsing

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import ru.petr.songapp.screens.songScreen.song.models.Song
import ru.petr.songapp.screens.songScreen.song.models.songParts.SongPart
import java.io.InputStream

fun fetchAttribute(parser: XmlPullParser, attrName: String): String {
    for (attrI in 0 until parser.attributeCount) {
        if (parser.getAttributeName(attrI) == attrName) {
            return parser.getAttributeValue(attrI)
        }
    }
    throw IllegalArgumentException("Attribute '$attrName' is not found")
}

fun fetchAttributes(parser: XmlPullParser): Map<String, String> {
    val attributes = mutableMapOf<String, String>()
    for (attrI in 0 until parser.attributeCount) {
        attributes[parser.getAttributeName(attrI)] = parser.getAttributeValue(attrI)
    }
    return attributes
}

typealias PartBuilderFunc = (Song.LayerStack, String, Map<String, String>) ->SongPart

internal fun instantiateNewParser(source: InputStream): XmlPullParser {
    val factory = XmlPullParserFactory.newInstance()
    factory.isNamespaceAware = true
    val parser: XmlPullParser = factory.newPullParser()
    parser.setInput(source, "UTF-8")
    return parser
}