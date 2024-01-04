package ru.petr.songapp.database.room.songData.utils

import android.content.Context
import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import ru.petr.songapp.database.room.SongAppDB
import ru.petr.songapp.database.room.songData.SongDBModel
import ru.petr.songapp.database.room.songData.SongCollectionDBModel
import ru.petr.songapp.database.room.songData.SongData
import ru.petr.songapp.database.room.songData.dao.SongDataForCollection
import ru.petr.songapp.screens.songScreen.song.models.parsing.TagAndAttrNames

const val INFO_FILE_EXT = "info"
const val COLLECTIONS_FOLDER = "collections"
const val COLLECTION_INFO_FILE = "collection.$INFO_FILE_EXT"

private const val LOG_TAG = "create_db_utils"

suspend fun populateDBFromAssets(appContext: Context,
                                 database: SongAppDB,
                                 curDBPopulation: Map<String, Pair<Int, List<SongDataForCollection>>> = mapOf()) {
    if (!curDBPopulation.containsKey("Избранное")) {
        val favoriteSongsDbModel = SongCollectionDBModel(0, "Избранное", "Избранное")
        database.SongCollectionDao().insert(favoriteSongsDbModel)
    }

    appContext.assets.list("$COLLECTIONS_FOLDER/")?.forEach { collection ->
        val collectionId = if (!curDBPopulation.containsKey(collection)) {
            val shortCollectionName: String = getShortCollectionName(appContext, collection)
            database.SongCollectionDao().insert(SongCollectionDBModel(0, collection, shortCollectionName)).toInt()
        } else {
            curDBPopulation[collection]!!.first
        }

        appContext.assets.list("$COLLECTIONS_FOLDER/$collection/")?.forEach { songFile ->
            if (!songFile.endsWith(".$INFO_FILE_EXT") &&
                ((curDBPopulation[collection] == null) || (curDBPopulation[collection] != null) &&
                (!isSongAlreadyInCollection(songFile, curDBPopulation[collection]!!.second)))) {
                val factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val parser: XmlPullParser = factory.newPullParser()
                val newSong = parseSongFile(appContext, parser, songFile, collectionId, collection)
                database.SongDao().insert(newSong)
            }
        }
    }
}

fun isSongAlreadyInCollection(songFileName: String,
                              collection: List<SongDataForCollection>) : Boolean {
    val songNum = songFileName.split(' '/*, limit = 1*/)[0].toInt()
    return collection.firstOrNull { it.numberInCollection == songNum } != null
}

fun getShortCollectionName(appContext: Context, collectionName: String): String {
    return appContext.assets
        .open("$COLLECTIONS_FOLDER/$collectionName/$COLLECTION_INFO_FILE")
        .bufferedReader().readText()
}

fun parseSongFile(appContext: Context, parser: XmlPullParser, songFile: String, collectionId: Int, collectionName: String): SongDBModel {
    val file = appContext.assets.open("$COLLECTIONS_FOLDER/$collectionName/$songFile")
    Log.d(LOG_TAG, "parsing file: $songFile")
    parser.setInput(file, "UTF-8")
    var numberInCollection = 0
    var name = ""
    var isCanon = false
    var textAuthors = ""
    var textRusAuthors = ""
    var musicComposers = ""
    var additionalInfo = ""
    var plainText = ""
    while (parser.eventType != XmlPullParser.END_DOCUMENT) {
        if (parser.eventType == XmlPullParser.START_TAG && parser.name == TagAndAttrNames.SONG_TAG._name) {
            for (attrI in 0 until parser.attributeCount) {
                when (parser.getAttributeName(attrI)) {
                    TagAndAttrNames.NUMBER_ATTR._name -> { numberInCollection = parser.getAttributeValue(attrI).toInt() }
                    TagAndAttrNames.NAME_ATTR._name -> { name = parser.getAttributeValue(attrI) }
                    TagAndAttrNames.CANON_ATTR._name -> { isCanon = parser.getAttributeValue(attrI).toBoolean() }
                    TagAndAttrNames.TEXT_ATTR._name -> { textAuthors = parser.getAttributeValue(attrI) }
                    TagAndAttrNames.TEXT_RUS_ATTR._name -> { textRusAuthors = parser.getAttributeValue(attrI) }
                    TagAndAttrNames.MUSIC_ATTR._name -> { musicComposers = parser.getAttributeValue(attrI) }
                    TagAndAttrNames.ADDITIONAL_INFO_ATTR._name -> { additionalInfo = parser.getAttributeValue(attrI) }
                }
            }
        }
        if (parser.text != null) {
            plainText += parser.text.trim() + (if (!plainText.endsWith(" ")) " " else "")
        }
        parser.next()
    }
    return SongDBModel(
            0,
            collectionId,
            SongData(
                name,
                numberInCollection,
                false,
                isCanon,
                textAuthors,
                textRusAuthors,
                musicComposers,
                additionalInfo,
                true, // all songs from included files are fixed
            ),
            appContext.assets
            .open("$COLLECTIONS_FOLDER/$collectionName/$songFile")
            .bufferedReader().readText(),
            plainText,
    )
}