package ru.petr.songapp.screens.songListScreen.songList.scrollbar

import com.arkivanov.decompose.value.Value

interface ScrollbarComponent {
    val isBright: Value<Boolean>
    val scrollOffset: Value<Float>
    val numbersAreShown: Value<Boolean>
    val itemsQty: Value<Int>
    val listScrollIsEnabled: Value<Boolean>
    val targetListOffset: Value<Int>
    val targetListIndex: Value<Int>

    fun updateItemsQty(itemsQty: Int)

    fun setNumberNeed(need: Boolean)

    fun onPress()

    fun onReleaseOrCancel()

    fun onDrag(offset: Float)

    fun setColumnHeight(height: Float)

    fun setPointerHeight(height: Float)

    fun updateListScrollOffset(index: Int, offset: Int)

    fun setItemHeight(height: Float)
}