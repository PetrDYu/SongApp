package ru.petr.songapp.screens.songListScreen.songList.scrollbar

import com.arkivanov.decompose.value.Value

interface ScrollbarComponent {
    // Internal for ScrollbarContent
    val isBright: Value<Boolean>
    val scrollOffset: Value<Float>
    val numbersAreShown: Value<Boolean>
    val itemsQty: Value<Int>
    val numbersList: Value<List<Int>>
    val pxBetweenNumbers: Value<Float>

    fun onPress()
    fun onReleaseOrCancel()
    fun onDrag(offset: Float)
    fun setPointerHeight(height: Float)
    fun setTextSizeInPx(textSize: Float)


    // External
    val listScrollIsEnabled: Value<Boolean>
    val targetListOffset: Value<Int>
    val targetListIndex: Value<Int>

    fun updateItemsQty(itemsQty: Int)
    fun setNumberNeed(need: Boolean)
    fun setColumnHeight(height: Float)
    fun updateListScrollOffset(index: Int, offset: Int)
    fun setItemHeight(height: Float)
}