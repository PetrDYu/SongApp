package ru.petr.songapp.screens.songListScreen.songList.scrollbar

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update

const val NUMBER_TO_EMPTY_SPACE_MAX_FRACTION: Float = 0.2f

val ROUND_DISTANCES = listOf(1, 5, 10, 20, 50, 100, 200, 250, 500, 1000)

class DefaultScrollbarComponent(
    componentContext: ComponentContext
) : ScrollbarComponent, ComponentContext by componentContext
{
    private var _isBright = MutableValue(false)
    override val isBright: Value<Boolean> = _isBright

    private var _scrollOffset = MutableValue(0f)
    override val scrollOffset: Value<Float> = _scrollOffset

    private var _isVisible = MutableValue(false)
    override val isVisible: Value<Boolean> = _isVisible

    private var _itemsQty = MutableValue(0)
    override val itemsQty: Value<Int> = _itemsQty
    
    private var _numbersList = MutableValue(emptyList<Int>())
    override val numbersList: Value<List<Int>> = _numbersList

    private var _numbersPositions = MutableValue(emptyList<Float>())
    override val numbersPositions: Value<List<Float>> = _numbersPositions

    private var _listScrollIsEnabled = MutableValue(false)
    override val listScrollIsEnabled: Value<Boolean> = _listScrollIsEnabled

    private var _targetListIndex = MutableValue(0)
    override val targetListIndex: Value<Int> = _targetListIndex

    private var _targetListOffset = MutableValue(0)
    override val targetListOffset: Value<Int> = _targetListOffset

    private var columnHeight = 0f

    private var _pointerHeight = MutableValue(0f)
    override val pointerHeight: Value<Float> = _pointerHeight

    private var dragWithPointer = false

    private var itemHeight = 0f

    private val listHeight
        get() = _itemsQty.value * itemHeight

    private val maxPointerOffset: Float
        get() = (columnHeight - _pointerHeight.value).coerceAtLeast(0f)

    private var textSizeInPx = 0f

    private var itemNumbers: List<Int> = emptyList()

    private var _currentSongNumber = MutableValue(0)
    override val currentSongNumber: Value<Int> = _currentSongNumber

    override fun setItemNumbersList(numbers: List<Int>) {
        itemNumbers = numbers
        _itemsQty.update { numbers.size }
        calculateScrollbarTextParams()
    }

    override fun scrollbarNeed(need: Boolean) {
        _isVisible.update { need }
    }

    override fun onPress() {
        _isBright.update { true }
        dragWithPointer = true
        updateCurrentSongNumber()
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
        updateCurrentSongNumber()
    }

    override fun setColumnHeight(newHeight: Float) {
        columnHeight = newHeight
        calculateScrollbarTextParams()
    }

    override fun setPointerHeight(height: Float) {
        _pointerHeight.update { height }
        calculateScrollbarTextParams()
    }

    override fun setTextSizeInPx(textSize: Float) {
        textSizeInPx = textSize
        calculateScrollbarTextParams()
    }

    override fun updateListScrollOffset(index: Int, offset: Int, fixListOffset: Boolean) {
        if (!dragWithPointer) {
            if (fixListOffset) {
                _targetListOffset.update { offset }
                _targetListIndex.update { index }
            }
            val offset = index * itemHeight + offset
            (listHeight - columnHeight).takeIf { it != 0f }?.let { actualListHeight ->
                val fraction = offset / actualListHeight
                _scrollOffset.update { fraction.coerceIn(0f, 1f) * maxPointerOffset }
            }
        }
    }

    override fun setItemHeight(height: Float) {
        itemHeight = height
        calculateScrollbarTextParams()
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

    private fun calculateScrollbarTextParams() {
        if ((listHeight - columnHeight) > 0 && itemHeight > 0 && maxPointerOffset > 0) {
            val itemsQtyValue = itemsQty.value
            for (distance in 1..itemsQtyValue) {
                val freeSpacePerNumber = calcFreeSpaceByDistance(distance)
                if (textSizeInPx / freeSpacePerNumber < NUMBER_TO_EMPTY_SPACE_MAX_FRACTION) {
                    var distanceToUse = distance
                    if (distanceToUse > 1)
                    {
                    for (round in ROUND_DISTANCES) {
                        if (distance / round == 0) {
                            distanceToUse = round
                            break
                        }
                    }
                        }
                    _numbersList.update {
                        (0..(itemsQtyValue - 1) step distanceToUse)
                            .map { itemNumbers[it] }
                            .toList()
                    }
                    val freeSpacePerNumberToUse = calcFreeSpaceByDistance(distanceToUse)
                    _numbersPositions.update {
                        (0..(itemsQtyValue - 1) step distanceToUse)
                            .mapIndexed { index, _ -> ((pointerHeight.value / 2 - textSizeInPx / 2) + (index * freeSpacePerNumberToUse)) }
                    }
                    break
                }
            }
        }
    }

    private fun calcFreeSpaceByDistance(distance: Int): Float {
        return distance * itemHeight * maxPointerOffset / (listHeight - columnHeight)
    }

    private fun updateCurrentSongNumber() {
        if (itemHeight > 0f && itemsQty.value > 0 && targetListIndex.value < itemNumbers.size) {
            _currentSongNumber.update {
                itemNumbers.getOrNull(targetListIndex.value) ?: 0
            }
        }
    }
}