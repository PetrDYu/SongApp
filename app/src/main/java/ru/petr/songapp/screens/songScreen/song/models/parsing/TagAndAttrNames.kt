package ru.petr.songapp.screens.songScreen.song.models.parsing

enum class TagAndAttrNames(val _name: String) {
    // Tags
    REPEAT_TAG("repeat"),
    SONG_TAG("song"),
    VERSE_TAG("verse"),
    CHORUS_TAG("chorus"),
    BRIDGE_TAG("bridge"),
    STRING_TAG("string"),
    PLAIN_TAG("plain"),
    CHORD_TAG("chord"),

    //Attributes
    ID_ATTR("id"),
    LAYER_ID_ATTR("layer_id"),
    NUMBER_ATTR("number"),
    IS_OPENING_ATTR("is_opening"),
    NAME_ATTR("name"),
    CANON_ATTR("canon"),
    TEXT_ATTR("text"),
    TEXT_RUS_ATTR("textRus"),
    MUSIC_ATTR("music"),
    ADDITIONAL_INFO_ATTR("additionalInfo"),

    REP_RATE_ATTR("rep_rate"),

    MAIN_CHORD("main_chord"),
    CHORD_IS_MINOR("chord_is_minor"),
    CHORD_SIGN("chord_sign"),
    CHORD_TYPE("chord_type"),

    // Non XML attributes

}