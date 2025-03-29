package ru.petr.songapp.screens.songListScreen.songList.scrollbar

import com.arkivanov.decompose.value.Value

interface ScrollbarComponent {
    val isBright: Value<Boolean>
    val scrollOffset: Value<Float>
    val numbersAreShown: Value<Boolean>
    val itemsQty: Value<Int>

    fun updateItemsQty(itemsQty: Int)

    fun setNumberNeed(need: Boolean)

    fun onPress()

    fun onReleaseOrCancel()

    fun onDrag(offset: Float)

    fun setColumnHeight(height: Float)

    fun setPointerHeight(height: Float)
}