package ru.petr.songapp.screens.songScreen.song.models

data class SongSettings(
    val songFontSize: Int,
    val proModeIsActive: Boolean,

    val setSongFontSize: (newFontSize: Int)->Unit
)
