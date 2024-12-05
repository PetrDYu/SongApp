package ru.petr.songapp.database.room.songData.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.first
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import ru.petr.songapp.R
import ru.petr.songapp.database.room.SongAppDB
import ru.petr.songapp.database.room.songData.SongDBModel
import ru.petr.songapp.database.room.songData.SongCollectionDBModel
import ru.petr.songapp.database.room.songData.SongData
import ru.petr.songapp.database.room.songData.dao.SongDataForCollection
import ru.petr.songapp.screens.songScreen.song.models.parsing.TagAndAttrNames
import java.io.File

const val INFO_FILE_EXT = "info"
const val COLLECTIONS_FOLDER = "collections"
const val COLLECTION_INFO_FILE = "collection.$INFO_FILE_EXT"
const val SONGS_VERSION_FILE = "version"

private const val LOG_TAG = "create_db_utils"

suspend fun populateDBFromAssets(appContext: Context,
                                 database: SongAppDB,
                                 curDBPopulation: Map<String, Pair<Int, List<SongDataForCollection>>> = mapOf(),
                                 updatingProgress: (Float) -> Unit) {
    val songsVersionFile = File(appContext.filesDir, SONGS_VERSION_FILE)
    var needUpdateDB = false
    if (songsVersionFile.exists().not()) {
        songsVersionFile.createNewFile()
        needUpdateDB = true
    } else {
        val songsVersion = songsVersionFile.readText().toInt()
        if (songsVersion != appContext.resources.getInteger(R.integer.songs_version)) {
            needUpdateDB = true
        } else {
            updatingProgress(1f)
        }
    }

    var favoriteSongs: List<SongDataForCollection>?= null
    if (needUpdateDB) {
        songsVersionFile.writeText("${appContext.resources.getInteger(R.integer.songs_version)}")
        // Get songs from favorites
        favoriteSongs = database.SongDao().getAllFavoriteSongs().first()
        database.clearAllTables()
        updatingProgress(0f)
    }

    if (!curDBPopulation.containsKey("Избранное") || needUpdateDB) {
        val favoriteSongsDbModel = SongCollectionDBModel(0, "Избранное", "Избранное")
        database.SongCollectionDao().insert(favoriteSongsDbModel)
    }

    // Count songs in assets
    val songsCount = appContext.assets.list("$COLLECTIONS_FOLDER/")?.sumOf { collection ->
        appContext.assets.list("$COLLECTIONS_FOLDER/$collection/")?.count { !it.endsWith(".$INFO_FILE_EXT") } ?: 0
    } ?: 0

    var songsAlreadyInDBCount = 0
    if (!needUpdateDB) {
        songsAlreadyInDBCount = database.SongDao().getSongsCount().first()
        updatingProgress(songsAlreadyInDBCount.toFloat() / songsCount.toFloat())
    }

    Log.d(LOG_TAG, "songs count: $songsCount")
    Log.d(LOG_TAG, "songs already in db: $songsAlreadyInDBCount")

    appContext.assets.list("$COLLECTIONS_FOLDER/")?.forEach { collection ->
        val collectionId = if (!curDBPopulation.containsKey(collection) || needUpdateDB) {
            val shortCollectionName: String = getShortCollectionName(appContext, collection)
            database.SongCollectionDao().insert(SongCollectionDBModel(0, collection, shortCollectionName)).toInt()
        } else {
            curDBPopulation[collection]!!.first
        }

        appContext.assets.list("$COLLECTIONS_FOLDER/$collection/")?.forEach { songFile ->
            if (!songFile.endsWith(".$INFO_FILE_EXT") &&
                ((curDBPopulation[collection] == null) ||
                 ((curDBPopulation[collection] != null) && (!isSongAlreadyInCollection(songFile, curDBPopulation[collection]!!.second))) ||
                 needUpdateDB)) {
                val factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val parser: XmlPullParser = factory.newPullParser()
                val newSong = parseSongFile(appContext, parser, songFile, collectionId, collection)
                val songId = database.SongDao().insert(newSong)
                favoriteSongs?.let { favSongs ->
                    database.SongDao().updateFavorite(songId.toInt(), isSongInFavorites(newSong, favSongs))
                }
                songsAlreadyInDBCount++
                updatingProgress(songsAlreadyInDBCount.toFloat() / songsCount.toFloat())
                Log.d(LOG_TAG, "updating progress:$songsAlreadyInDBCount / $songsCount")
            }
        }
    }
}

// Check if song in favorites
fun isSongInFavorites(songDBModel: SongDBModel,
                             favoriteSongs: List<SongDataForCollection>) : Boolean {
    val songNum = songDBModel.songData.numberInCollection
    val songInFavorites = favoriteSongs.filter { it.numberInCollection == songNum }

    if (songInFavorites.isEmpty()) {
        return false
    } else {
        songInFavorites.forEach { song ->
            if (song.name == songDBModel.songData.name) {
                return true
            } else if (song.name.startsWith(songDBModel.songData.name)) {
                return true
            }
        }
    }
    return false
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
    val (plainTextWithoutSpecialSymbol, specialSymbolsPositions) = removeSpecialSymbolsAndGetPositions(plainText)
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
            plainTextWithoutSpecialSymbol,
            specialSymbolsPositions,
    )
}

private val specialSymbolsRegExp = """[^A-Za-zА-Яа-я0-9\s]""".toRegex()
private val spaceNotOneRegExp = """\s{2,}""".toRegex()

fun removeSpecialSymbolsAndGetPositions(plainText: String) : Pair<String, String> {
    val specPosList = mutableListOf<Int>()
    specialSymbolsRegExp.findAll(plainText).forEach { specPos ->
        if (specPos.range.first != 0) {
            if (plainText[specPos.range.first - 1] == ' ' && plainText[specPos.range.last + 1] == ' ') {
                specPosList.add(specPos.range.first - 1)
            }
        }
        specPosList.add(specPos.range.first)
    }
    val textWithoutSpecialSymbols = specialSymbolsRegExp.replace(plainText, "")
    val spacePosList = mutableListOf<Int>()
    spaceNotOneRegExp.findAll(plainText)
        .forEach { spacesPos ->
            spacePosList.addAll((spacesPos.range.first + 1 ..spacesPos.range.last).toList())
        }
    specPosList.addAll(spacePosList)
    return Pair(spaceNotOneRegExp.replace(textWithoutSpecialSymbols, " "), specPosList.sorted().joinToString(","))
}

fun getIndexTuning(curIndex: Int, posList: List<Int>): Int {
    var indexTuning = 0
    posList.forEach { pos ->
        if (curIndex + indexTuning > pos) {
            indexTuning++
        }
    }
    return indexTuning
}

fun getIndexTuning(curIndex: Int, posList: String): Int {
    return getIndexTuning(curIndex, posList.split(",").map { it.toInt() })
}

fun getLenTuning(startIndex: Int, curLen: Int, posList: String): Int {
    return getIndexTuning(startIndex + curLen - 1, posList) - getIndexTuning(startIndex, posList)
}