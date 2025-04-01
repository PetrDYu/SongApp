package ru.petr.songapp.screens.songListScreen.songList.scrollbar

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.SupervisorJob
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

class DefaultScrollbarComponent(
    componentContext: ComponentContext,
    mainCoroutineContext: CoroutineContext = SupervisorJob())
    : ScrollbarComponent, ComponentContext by componentContext
{
    private val coroutineScope = componentContext.coroutineScope(mainCoroutineContext)

    private var _isBright = MutableValue(false)
    override val isBright: Value<Boolean> = _isBright

    private var _scrollOffset = MutableValue(0f)
    override val scrollOffset: Value<Float> = _scrollOffset

    private var _numbersAreShown = MutableValue(false)
    override val numbersAreShown: Value<Boolean> = _numbersAreShown

    private var _itemsQty = MutableValue(0)
    override val itemsQty: Value<Int> = _itemsQty

    private var _listScrollIsEnabled = MutableValue(false)
    override val listScrollIsEnabled: Value<Boolean> = _listScrollIsEnabled

    private var _targetListIndex = MutableValue(0)
    override val targetListIndex: Value<Int> = _targetListIndex

    private var _targetListOffset = MutableValue(0)
    override val targetListOffset: Value<Int> = _targetListOffset

    private var columnHeight = 0f

    private var pointerHeight = 0f

    private var dragWithPointer = false

    private var itemHeight = 0f

    private val listHeight
        get() = _itemsQty.value * itemHeight

    private val maxPointerOffset: Float
        get() = (columnHeight - pointerHeight).coerceAtLeast(0f)


    override fun updateItemsQty(itemsQty: Int) {
        _itemsQty.update { itemsQty }
    }

    override fun setNumberNeed(need: Boolean) {
        _numbersAreShown.update { need }
    }

    override fun onPress() {
        _isBright.update { true }
        dragWithPointer = true
    }

    override fun onReleaseOrCancel() {
        _isBright.update { false }
        dragWithPointer = false
    }

    override fun onDrag(offset: Float) {
        _scrollOffset.update { curOffset ->
            (curOffset + offset).coerceIn(0f, maxPointerOffset)
        }
        calcListOffsetParams(_scrollOffset.value / maxPointerOffset)
    }

    override fun setColumnHeight(newHeight: Float) {
        columnHeight = newHeight
    }

    override fun setPointerHeight(height: Float) {
        pointerHeight = height
    }

    override fun updateListScrollOffset(index: Int, offset: Int) {
        if (!dragWithPointer) {
            val offset = index * itemHeight + offset
            (listHeight - columnHeight).takeIf { it != 0f }?.let { actualListHeight ->
                val fraction = offset / actualListHeight
                _scrollOffset.update { fraction.coerceIn(0f, 1f) * maxPointerOffset }
            }
        }
    }

    override fun setItemHeight(height: Float) {
        itemHeight = height
    }

    private fun calcListOffsetParams(fraction: Float) {
        val maxScrollPx = (listHeight - columnHeight).coerceAtLeast(0f)
        val desiredScrollPx = fraction * maxScrollPx
        _listScrollIsEnabled.update { itemHeight > 0f }
        _targetListIndex.update {
            if (itemHeight != 0f) (desiredScrollPx / itemHeight).toInt() else 0
        }
        _targetListOffset.update {
            if (itemHeight != 0f) (desiredScrollPx % itemHeight).toInt() else 0
        }
    }
}