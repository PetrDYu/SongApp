package ru.petr.songapp.screens.songListScreen.songList.scrollbar

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.serialization.builtins.serializer

// Maximum allowed fraction of text size to empty space ratio for number display
const val NUMBER_TO_EMPTY_SPACE_MAX_FRACTION: Float = 0.2f

// List of round numbers used for optimizing number display distances
val ROUND_DISTANCES = listOf(1, 5, 10, 20, 50, 100, 200, 250, 500, 1000)

/**
 * Default implementation of the ScrollbarComponent interface
 * Manages scrollbar state and logic including:
 * - Song number display and positioning
 * - Scroll position and offsets
 * - User interaction state
 * - Scrollbar visibility and appearance
 */
class DefaultScrollbarComponent(
    componentContext: ComponentContext
) : ScrollbarComponent, ComponentContext by componentContext {
    // Brightness state for visual feedback when scrollbar is active
    private var _isBright = MutableValue(false)
    override val isBright: Value<Boolean> = _isBright

    // Current scroll position with state preservation
    private var _scrollOffset = MutableValue(stateKeeper.consume("scrollbar_offset", Float.serializer()) ?: 0f)
    override val scrollOffset: Value<Float> = _scrollOffset

    // Visibility state of the scrollbar
    private var _isVisible = MutableValue(false)
    override val isVisible: Value<Boolean> = _isVisible

    // Total number of items in the list
    private var _itemsQty = MutableValue(0)
    override val itemsQty: Value<Int> = _itemsQty
    
    // List of numbers to display on the scrollbar
    private var _numbersList = MutableValue(emptyList<Int>())
    override val numbersList: Value<List<Int>> = _numbersList

    // Positions (Y coordinates) for displaying numbers
    private var _numbersPositions = MutableValue(emptyList<Float>())
    override val numbersPositions: Value<List<Float>> = _numbersPositions

    // Flag indicating whether scrolling the list is enabled
    private var _listScrollIsEnabled = MutableValue(false)
    override val listScrollIsEnabled: Value<Boolean> = _listScrollIsEnabled

    // Target index in the list to scroll to
    private var _targetListIndex = MutableValue(0)
    override val targetListIndex: Value<Int> = _targetListIndex

    // Pixel offset for fine-tuned scrolling
    private var _targetListOffset = MutableValue(0)
    override val targetListOffset: Value<Int> = _targetListOffset

    // Height of the scrollbar column
    private var columnHeight = 0f

    // Height of the scrollbar pointer/handle
    private var _pointerHeight = MutableValue(0f)
    override val pointerHeight: Value<Float> = _pointerHeight

    // Flag indicating whether scrolling is being performed with pointer
    private var dragWithPointer = false

    // Height of a single item in the song list
    private var itemHeight = 0f

    // Computed total height of all items in the list
    private val listHeight
        get() = _itemsQty.value * itemHeight

    // Maximum allowed pointer offset to stay within column bounds
    private val maxPointerOffset: Float
        get() = (columnHeight - _pointerHeight.value).coerceAtLeast(0f)

    // Text size in pixels for calculating number display
    private var textSizeInPx = 0f

    // Actual item numbers (song numbers) for display
    private var itemNumbers: List<Int> = emptyList()

    // Current song number being displayed in tooltip
    private var _currentSongNumber = MutableValue(0)
    override val currentSongNumber: Value<Int> = _currentSongNumber

    init {
        // Register state keeper for preserving scroll position
        stateKeeper.register(
            key = "scrollbar_offset",
            strategy = Float.serializer()
        ) { _scrollOffset.value }
    }

    /**
     * Sets the list of song numbers and updates scrollbar calculations
     * @param numbers List of song numbers to display
     */
    override fun setItemNumbersList(numbers: List<Int>) {
        itemNumbers = numbers
        _itemsQty.update { numbers.size }
        calculateScrollbarTextParams()
    }

    /**
     * Sets scrollbar visibility
     * @param need Whether scrollbar should be visible
     */
    override fun scrollbarNeed(need: Boolean) {
        _isVisible.update { need }
    }

    /**
     * Handles press events on the scrollbar
     * Updates visual state and starts tracking drag operations
     */
    override fun onPress() {
        _isBright.update { true }
        dragWithPointer = true
        updateCurrentSongNumber()
    }

    /**
     * Handles release or cancel events on the scrollbar
     * Resets visual state and drag tracking
     */
    override fun onReleaseOrCancel() {
        _isBright.update { false }
        dragWithPointer = false
    }

    /**
     * Handles drag events on the scrollbar
     * Updates scroll position and recalculates list offsets
     * @param offset Vertical drag offset in pixels
     */
    override fun onDrag(offset: Float) {
        _scrollOffset.update { curOffset ->
            (curOffset + offset).coerceIn(0f, maxPointerOffset)
        }
        calcListOffsetParams(_scrollOffset.value / maxPointerOffset)
        updateCurrentSongNumber()
    }

    /**
     * Sets the height of the scrollbar column
     * @param newHeight New height value in pixels
     */
    override fun setColumnHeight(newHeight: Float) {
        columnHeight = newHeight
        calculateScrollbarTextParams()
    }

    /**
     * Sets the height of the scrollbar pointer/handle
     * @param height New height value in pixels
     */
    override fun setPointerHeight(height: Float) {
        _pointerHeight.update { height }
        calculateScrollbarTextParams()
    }

    /**
     * Sets the text size used for displaying numbers
     * @param textSize Font size in pixels
     */
    override fun setTextSizeInPx(textSize: Float) {
        textSizeInPx = textSize
        calculateScrollbarTextParams()
    }

    /**
     * Updates the scroll offset based on list position
     * @param index Item index in the list
     * @param offset Pixel offset within the item
     * @param fixListOffset Whether to remember this as a fixed position
     */
    override fun updateListScrollOffset(index: Int, offset: Int, fixListOffset: Boolean) {
        if (!dragWithPointer) {
            if (fixListOffset) {
                _targetListOffset.update { offset }
                _targetListIndex.update { index }
            }
            val listOffset = index * itemHeight + offset
            (listHeight - columnHeight).takeIf { it != 0f }?.let { actualListHeight ->
                val fraction = listOffset / actualListHeight
                _scrollOffset.update { fraction.coerceIn(0f, 1f) * maxPointerOffset }
            }
        }
    }

    /**
     * Sets the height of a single item in the list
     * @param height Item height in pixels
     */
    override fun setItemHeight(height: Float) {
        itemHeight = height
        calculateScrollbarTextParams()
    }

    /**
     * Calculates list offset parameters based on scroll fraction
     * @param fraction Scroll position as fraction of total scrollable area
     */
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

    /**
     * Calculates optimal parameters for displaying song numbers on the scrollbar
     * Uses spacing algorithm to ensure readable number display
     */
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

    /**
     * Calculates free space between numbers based on distance
     * @param distance Number of items to skip between displayed numbers
     * @return Amount of free space in pixels
     */
    private fun calcFreeSpaceByDistance(distance: Int): Float {
        return distance * itemHeight * maxPointerOffset / (listHeight - columnHeight)
    }

    /**
     * Updates the current song number based on list position
     * Used for displaying the current position in tooltip
     */
    private fun updateCurrentSongNumber() {
        if (itemHeight > 0f && itemsQty.value > 0 && targetListIndex.value < itemNumbers.size) {
            _currentSongNumber.update {
                itemNumbers.getOrNull(targetListIndex.value) ?: 0
            }
        }
    }
}