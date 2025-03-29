package ru.petr.songapp.screens.songListScreen.songList.scrollbar

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update

class DefaultScrollbarComponent(
    componentContext: ComponentContext) : ScrollbarComponent, ComponentContext by componentContext {

    private var _isBright = MutableValue(false)
    override val isBright: Value<Boolean> = _isBright

    private var _scrollOffset = MutableValue(0f)
    override val scrollOffset: Value<Float> = _scrollOffset

    private var _numbersAreShown = MutableValue(false)
    override val numbersAreShown: Value<Boolean> = _numbersAreShown

    private var _itemsQty = MutableValue(0)
    override val itemsQty: Value<Int> = _itemsQty

    private var columnHeight = 0f

    private var pointerHeight = 0f


    override fun updateItemsQty(itemsQty: Int) {
        _itemsQty.update { itemsQty }
    }

    override fun setNumberNeed(need: Boolean) {
        _numbersAreShown.update { need }
    }

    override fun onPress() {
        _isBright.update { true }
    }

    override fun onReleaseOrCancel() {
        _isBright.update { false }
    }

    override fun onDrag(offset: Float) {
        _scrollOffset.update { curOffset ->
            val newOffset = curOffset + offset
            if (newOffset < 0)
                0f
            else if (newOffset > (columnHeight - pointerHeight))
                columnHeight - pointerHeight
            else
                newOffset
        }
    }

    override fun setColumnHeight(newHeight: Float) {
        columnHeight = newHeight
    }

    override fun setPointerHeight(height: Float) {
        pointerHeight = height
    }
}