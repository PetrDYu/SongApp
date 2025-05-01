package ru.petr.songapp.screens.songListScreen.songList.scrollbar

import com.arkivanov.decompose.value.Value

class PreviewScrollbarComponent(
    override val isBright: Value<Boolean>,
    override val scrollOffset: Value<Float>,
    override val isVisible: Value<Boolean>,
    override val itemsQty: Value<Int>,
    override val numbersList: Value<List<Int>>,
    override val numbersPositions: Value<List<Float>>,
    override val pointerHeight: Value<Float>,
    override val currentSongNumber: Value<Int>
) : ScrollbarComponent {

    override fun onPress() {

    }

    override fun onReleaseOrCancel() {

    }

    override fun onDrag(offset: Float) {

    }

    override fun setPointerHeight(height: Float) {

    }

    override fun setTextSizeInPx(textSize: Float) {

    }

    override val listScrollIsEnabled: Value<Boolean>
        get() = TODO("Not yet implemented")
    override val targetListOffset: Value<Int>
        get() = TODO("Not yet implemented")
    override val targetListIndex: Value<Int>
        get() = TODO("Not yet implemented")

    override fun setItemNumbersList(numbers: List<Int>) {

    }

    override fun scrollbarNeed(need: Boolean) {

    }

    override fun setColumnHeight(height: Float) {

    }

    override fun updateListScrollOffset(
        index: Int,
        offset: Int,
        fixListOffset: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun setItemHeight(height: Float) {
        TODO("Not yet implemented")
    }
}