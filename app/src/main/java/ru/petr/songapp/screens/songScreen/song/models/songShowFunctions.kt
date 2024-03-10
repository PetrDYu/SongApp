package ru.petr.songapp.screens.songScreen.song.models

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import ru.petr.songapp.R
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.*
import ru.petr.songapp.screens.songScreen.song.models.songParts.SongPart
import ru.petr.songapp.screens.songScreen.song.models.songParts.linesAndChunks.LineChunk
import ru.petr.songapp.screens.songScreen.song.models.songParts.linesAndChunks.SongPartLine
import ru.petr.songapp.screens.songScreen.song.models.songParts.linesAndChunks.layers.ChunkLayer
import ru.petr.songapp.screens.songScreen.song.models.songParts.linesAndChunks.layers.ChunkText

@Composable
fun SongView(
    modifier: Modifier = Modifier,
    showType: SongShowTypes,
    song: Song,
    fontSize: Int,
    onChorusOffsetChanged: (Int, Int) -> Unit
) {
    Column(modifier.padding(top = 20.dp, start = 10.dp, end = 5.dp)) {
        for (part in song.mSongParts) {
            SongPartView(
                Modifier.onGloballyPositioned { layoutCoordinates ->
                    if (part is SongPart.Chorus) {
                        onChorusOffsetChanged(
                            layoutCoordinates.positionInParent().y.toInt() +
                            layoutCoordinates.parentCoordinates!!.positionInParent().y.toInt(),
                            layoutCoordinates.size.height
                        )
                    }
                },
                showType = showType,
                part = part,
                layerStack = song.mLayerStack,
                fontSize = fontSize,
            )
        }
    }
}

@Composable
fun SongPartView(
    modifier: Modifier = Modifier,
    showType: SongShowTypes,
    part: SongPart,
    layerStack: Song.LayerStack,
    fontSize: Int,
) {
    val (partType, titleId, number) = when (part) {
        is SongPart.Chorus -> {
            Triple(SongPart.SongPartTypes.CHORUS, R.string.chorus_title, -1)
        }
        is SongPart.Verse -> {
            Triple(SongPart.SongPartTypes.VERSE, R.string.verse_title, part.number)
        }
        is SongPart.Bridge -> {
            Triple(SongPart.SongPartTypes.BRIDGE, R.string.bridge_title, -1)
        }
    }
    Column(modifier) {
        SongPartTitleView(
            Modifier,
            showType,
            partType,
            titleId,
            number,
            fontSize)
        SongPartBodyView(showType = showType, part = part, layerStack = layerStack, fontSize = fontSize)
        Spacer(modifier = Modifier.height(fontSize.dp))
    }
}

@Composable
fun SongPartBodyView(modifier: Modifier = Modifier,
                     showType: SongShowTypes,
                     part: SongPart,
                     layerStack: Song.LayerStack,
                     fontSize: Int,
) {
    Column (
            modifier.padding(start=20.dp)
    ) {
        var nextLine: SongPartLine?
        var lineInd = 0
        var iterQty = 0
        var repQty: Int
        var prevRepQty = 0
        while (lineInd < part.lines.size) {
            LinesAnsBracketLayout() {
                Column() {
                    iterQty = 0
                    repQty = 0
                    do {
                        val line = part.lines[lineInd]
                        nextLine =
                            if (lineInd != part.lines.lastIndex) part.lines[lineInd + 1] else null
                        LineView(
                            showType = showType,
                            line = line,
                            layerStack = layerStack,
                            previousLine = if (lineInd != 0) part.lines[lineInd - 1] else null,
                            nextLine = nextLine,
                            fontSize = fontSize,
                        )
                        lineInd++
                        iterQty++
                        val nextLineConst = nextLine
                        prevRepQty = repQty
                        repQty = if (nextLineConst != null) {
                            line.hasSameRepeatLayerWithLineAndGetQty(nextLineConst)
                        } else {
                            0
                        }
                    } while (repQty != 0)
                }
                if (iterQty > 1) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.bracket),
                        contentDescription = null,
                        modifier = Modifier
                            .rotate(180f),
                        contentScale = ContentScale.FillBounds,
                    )
                    Text(" ${prevRepQty}p", fontSize = (fontSize * 1).sp)
                }
            }
        }
    }
}

@Composable
fun LinesAnsBracketLayout(
    modifier: Modifier = Modifier,
    spaceLinesToBracket: Float = 5f, // percents of lines width
    spaceForBracket: Float = 10f, // percents of full width
    content: @Composable () -> Unit
){
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        require(measurables.size == 3 || measurables.size == 1)

        if (measurables.size == 3) {
            val reqQtyPlaceable = measurables[2].measure(constraints)
            val linesPlaceable = measurables[0].measure(
                constraints.copy(
                    maxWidth = ((constraints.maxWidth * (1 - spaceForBracket / 100f) -
                                 reqQtyPlaceable.width) /
                                (1 + spaceLinesToBracket / 100f)).toInt()
                )
            )
            val bracketPlaceable = measurables[1].measure(
                constraints.copy(
                    minWidth = 0,
                    maxWidth = constraints.maxWidth - linesPlaceable.width - reqQtyPlaceable.width,
                    minHeight = linesPlaceable.height,
                    maxHeight = linesPlaceable.height
                )
            )

            val width = constraints.maxWidth
            val height = bracketPlaceable.height.coerceAtMost(linesPlaceable.height)

            val linesWidthWithSpaceToBracket = (linesPlaceable.width * (1 + spaceLinesToBracket / 100f)).toInt()

            layout(width, height) {
                linesPlaceable.placeRelative(0, 0)
                bracketPlaceable.placeRelative(linesWidthWithSpaceToBracket, 0)
                reqQtyPlaceable.placeRelative(
                    linesWidthWithSpaceToBracket + bracketPlaceable.width,
                    height / 2 - reqQtyPlaceable.height / 2)
            }
        } else {
            val linesPlaceable = measurables[0].measure(
                constraints = constraints
            )
            layout(constraints.maxWidth, linesPlaceable.height) {
                linesPlaceable.placeRelative(0, 0)
            }
        }
    }
}

@Composable
fun SongPartTitleView(
    modifier: Modifier = Modifier,
    showType: SongShowTypes,
    partType: SongPart.SongPartTypes,
    titleId: Int,
    number: Int,
    fontSize: Int,
) {
    val titleString = when (partType) {
        SongPart.SongPartTypes.VERSE -> {
            if (number != 0)
                "${stringResource(id = titleId)} $number:"
            else
                "${stringResource(id = titleId)}:"
        }

        SongPart.SongPartTypes.CHORUS,
        SongPart.SongPartTypes.BRIDGE -> {
            "${stringResource(id = titleId)}:"
        }
    }
    Text(
            titleString,
            fontSize = (fontSize * 0.75).sp,
            modifier = modifier.padding(bottom = (fontSize * 0.3).dp)
    )
}

@Composable
fun LineView(
    modifier: Modifier = Modifier,
    showType: SongShowTypes,
    line: SongPartLine,
    layerStack: Song.LayerStack,
    previousLine: SongPartLine?,
    nextLine: SongPartLine?,
    fontSize: Int,
) {
    val layersInCurrentLine: MutableList<ChunkLayer> = mutableListOf()
    line.chunks.forEach { chunk ->
            chunk.layers.forEach { layer ->
                if (null == layersInCurrentLine.find { it.isSimilarWithLayer(layer) }) {
                    layersInCurrentLine.add(layer)
                }
            }
    }
    SongTextAdaptiveContentLayout(modifier.padding(bottom = (fontSize * 0.3).dp)) {
        val chunksList = line.getChunksSplitByWords()

        var chunkInd = 0
        while(chunkInd < chunksList.size) {
            Row {
                do {
                    val chunk = chunksList[chunkInd]
                    ChunkView(
                        showType = showType,
                        chunk = chunk,
                        layerStack = layerStack,
                        previousChunk = if (chunkInd != 0) chunksList[chunkInd - 1] else null,
                        nextChunk = if (chunkInd != chunksList.lastIndex) chunksList[chunkInd + 1] else null,
                        isLayerMultiline = { layer ->
                            var result = false
                            previousLine?.let { line ->
                                if (line.chunks.isNotEmpty()) {
                                    result = line.chunks[line.chunks.lastIndex].hasSameLayer(layer)
                                }
                            }
                            nextLine?.let { line ->
                                if (line.chunks.isNotEmpty()) {
                                    result = result || line.chunks[0].hasSameLayer(layer)
                                }
                            }
                            result
                        },
                        fontSize = fontSize,
                        layersInCurrentLine = layersInCurrentLine,
                    )
                    chunkInd++
                } while ((!chunk.text!!.text.endsWith(" ")) && (chunkInd < chunksList.size))
            }
        }
    }
}

@Composable
fun ChunkView(
    modifier: Modifier = Modifier,
    showType: SongShowTypes,
    layerStack: Song.LayerStack,
    chunk: LineChunk,
    previousChunk: LineChunk? = null,
    nextChunk: LineChunk? = null,
    isLayerMultiline: (layer: ChunkLayer) -> Boolean,
    fontSize: Int,
    layersInCurrentLine: List<ChunkLayer>
) {
    Box(modifier = modifier) {
        when (showType) {
            SongShowTypes.VIEW -> {
                Column {
                    for (layer in layerStack.activeAddingLayers) {
                        val chunkAddingLayer = chunk.getSimilarLayer(layer)
                        if (chunkAddingLayer != null) {
                            ShowAddingLayer(layer = (chunkAddingLayer as ChunkLayer.AddingLayer), fontSize)
                        } else {
                            if (null != layersInCurrentLine.find { it.isSimilarWithLayer(layer) }) {
                                ShowEmptyAddingLayer(layer = layer, fontSize)
                            }
                        }
                    }

                    ChunkTextView(
                            chunk.text,
                            chunk.layers
                            .filter { layer -> null != layerStack.activeWrappingLayers.find{ it.isSimilarWithLayer(layer) } }
                            .map { it as ChunkLayer.WrappingLayer },
                            {layer -> previousChunk?.hasSameLayer(layer) ?: false },
                            {layer -> nextChunk?.hasSameLayer(layer) ?: false },
                            isLayerMultiline,
                            fontSize,
                    )
                }
            }
            SongShowTypes.EDIT -> {}
        }
    }
}

@Composable
fun ChunkTextView(text: ChunkText?,
                  chunkWrappingLayers: List<ChunkLayer.WrappingLayer>,
                  previousChunkHasSameLayer: (layer: ChunkLayer.WrappingLayer) -> Boolean,
                  nextChunkHasSameLayer: (layer: ChunkLayer.WrappingLayer) -> Boolean,
                  isLayerMultiline: (layer: ChunkLayer.WrappingLayer) -> Boolean,
                  fontSize: Int,
) {
    requireNotNull(text)
    var newChunkText: ChunkText = text
    var newTextStyle = LocalTextStyle.current.copy()
    chunkWrappingLayers.forEach {
        val textAndStyle = it.modifyTextAndStyle(
            newChunkText,
            newTextStyle,
            isStart = !previousChunkHasSameLayer(it),
            isEnd = !nextChunkHasSameLayer(it),
            isMultiline = isLayerMultiline(it),
            LocalContext.current,
        )
        newChunkText = textAndStyle.first
        newTextStyle = textAndStyle.second
    }
    Text(text = newChunkText.text, style = newTextStyle, fontSize = fontSize.sp, lineHeight = (fontSize * 1.05).sp)
}

@Composable
private fun SongTextAdaptiveContentLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(modifier = modifier, content = content) { measurables, outerConstraints ->
        val rowElementsCounts = mutableListOf(0)
        val maxRowHeights = mutableListOf(0)
        var maxRowSize = 0
        var rowNumber = 0
        var currentRowSize = 0
        val placeables = measurables.mapIndexed { _, measureable ->
            val placeable = measureable.measure(outerConstraints)
            if (currentRowSize != 0 &&
                ((currentRowSize + placeable.width) > outerConstraints.maxWidth)) {
                currentRowSize = 0
                rowElementsCounts.add(0)
                maxRowHeights.add(0)
                rowNumber++
            }
            currentRowSize += placeable.width
            if (maxRowSize < currentRowSize) {
                maxRowSize = currentRowSize
            }
            rowElementsCounts[rowNumber]++
            if (placeable.height > maxRowHeights[rowNumber]) {
                maxRowHeights[rowNumber] = placeable.height
            }
            placeable
        }

        val layoutHeight = maxRowHeights.sum()

        layout(
            width = outerConstraints.constrainWidth(maxRowSize),
            height = outerConstraints.constrainHeight(layoutHeight)
        ) {
            var curPlaceableIndex = 0
            var xPosition = 0
            var yPosition = 0
            rowElementsCounts.forEachIndexed { rowIndex, count ->
                for(plIndex in curPlaceableIndex until count + curPlaceableIndex) {
                    placeables[plIndex].placeRelative(xPosition, yPosition)
                    xPosition += placeables[plIndex].width
                }
                xPosition = 0
                yPosition += maxRowHeights[rowIndex]
                curPlaceableIndex += count
            }
        }
    }
}


/*
@Preview
@Composable
fun SongPreview() {
    val songColl = SongCollectionDBModel(1, "Test", "Test")
//    val song = Song.getSong(, false, songColl)
//    SongView(showType = SongShowTypes.VIEW, song = )
}*/
