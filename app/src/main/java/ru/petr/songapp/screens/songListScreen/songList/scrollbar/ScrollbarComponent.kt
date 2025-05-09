package ru.petr.songapp.screens.songListScreen.songList.scrollbar

import com.arkivanov.decompose.value.Value

/**
 * Interface defining the scrollbar component functionality
 * Handles all scrollbar state and user interactions
 */
interface ScrollbarComponent {
    // ----- Internal properties and methods for ScrollbarContent -----
    
    /**
     * Whether the scrollbar is in highlighted/active state
     */
    val isBright: Value<Boolean>
    
    /**
     * Current vertical offset of the scrollbar pointer in pixels
     */
    val scrollOffset: Value<Float>
    
    /**
     * Whether the scrollbar is currently visible
     */
    val isVisible: Value<Boolean>
    
    /**
     * Total number of items in the song list
     */
    val itemsQty: Value<Int>
    
    /**
     * List of numbers to display on the scrollbar track
     */
    val numbersList: Value<List<Int>>
    
    /**
     * Vertical positions (Y-coordinates) of numbers on the scrollbar
     */
    val numbersPositions: Value<List<Float>>
    
    /**
     * Height of the scrollbar pointer/handle in pixels
     */
    val pointerHeight: Value<Float>
    
    /**
     * Current song number to display in the tooltip
     */
    val currentSongNumber: Value<Int>

    /**
     * Called when the scrollbar pointer is pressed
     */
    fun onPress()
    
    /**
     * Called when the scrollbar pointer is released or drag is canceled
     */
    fun onReleaseOrCancel()
    
    /**
     * Called when the scrollbar pointer is dragged
     * @param offset Vertical drag offset in pixels
     */
    fun onDrag(offset: Float)
    
    /**
     * Sets the height of the scrollbar pointer
     * @param height Height in pixels
     */
    fun setPointerHeight(height: Float)
    
    /**
     * Sets the text size used for displaying numbers
     * @param textSize Text size in pixels
     */
    fun setTextSizeInPx(textSize: Float)


    // ----- External properties and methods for integration with list components -----
    
    /**
     * Whether scrolling the list is currently enabled
     */
    val listScrollIsEnabled: Value<Boolean>
    
    /**
     * Target scroll offset for the list in pixels
     */
    val targetListOffset: Value<Int>
    
    /**
     * Target item index to scroll to in the list
     */
    val targetListIndex: Value<Int>

    /**
     * Sets the list of song numbers to display on the scrollbar
     * @param numbers List of song numbers
     */
    fun setItemNumbersList(numbers: List<Int>)
    
    /**
     * Controls the visibility of the scrollbar
     * @param need Whether the scrollbar should be visible
     */
    fun scrollbarNeed(need: Boolean)
    
    /**
     * Sets the height of the scrollbar column
     * @param height Height in pixels
     */
    fun setColumnHeight(height: Float)
    
    /**
     * Updates the scrollbar position based on list scroll position
     * @param index Current visible item index in the list
     * @param offset Offset in pixels within the current item
     * @param fixListOffset Whether to remember this as a fixed position
     */
    fun updateListScrollOffset(index: Int, offset: Int, fixListOffset: Boolean)
    
    /**
     * Sets the height of each item in the list
     * @param height Height in pixels
     */
    fun setItemHeight(height: Float)
}