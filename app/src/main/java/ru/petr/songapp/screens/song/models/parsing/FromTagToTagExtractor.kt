package ru.petr.songapp.screens.song.models.parsing

class FromTagToTagExtractor {
    private var mStartLine = -1
    private var mStartColumn = -1
    private var mEndLine = -1
    private var mEndColumn = -1

    fun setStartPoint(line: Int, column: Int) {
        mStartLine = line
        mStartColumn = column
    }

    fun setEndPoint(line: Int, column: Int) {
        mEndLine = line
        mEndColumn = column
    }

    fun clean() {
        mStartLine = -1
        mStartColumn = -1
        mEndLine = -1
        mEndColumn = -1
    }

    fun extractPart(content: String): String {
        if (mStartLine <= 0 || mStartColumn <= 0 || mEndLine <= 0 || mEndColumn <= 0) {
            throw IllegalArgumentException("mStartLine ($mStartLine), mStartColumn ($mStartColumn), " +
                    "mEndLine ($mEndLine) and endColumn ($mEndColumn) should be positive")
        }
        // Check if start line number no greater than end line number
        if (mStartLine > mEndLine) {
            throw IllegalArgumentException("mStartLine ($mStartLine) shouldn't be " +
                    "greater than mEndLine ($mEndLine)")
        }
        // Split content into lines
        val splitContent = content.lines()
        // Check if content contains no less lines than end line number
        if (splitContent.size < mEndLine) {
            throw IllegalArgumentException("Content doesn't have enough lines (according mEndLine)")
        }
        // Find start bracket of opening tag from mStartColumn and backward
        val lastTagStart = splitContent[mStartLine - 1].lastIndexOf('<', startIndex=mStartColumn - 1)
        // Check if opening tag start bracket is found
        if (lastTagStart == -1) {
            throw IllegalArgumentException("Content doesn't contain an open tag bracket before " +
                    "start point (mStartLine and mStartColumn)")
        }

        // If extracting content is located in one line of content return substring of this line
        if (mStartLine == mEndLine) {
            return splitContent[mStartLine - 1].substring(lastTagStart, mEndColumn)
        }

        /* If the extracting content is located on several lines create list with:
        *   - start line from opening tag start bracket
        *   - all whole lines from start line to end line (excluded)
        *   - end line from start to end column*/
        val resultMas = mutableListOf<String>()
        resultMas.add(splitContent[mStartLine - 1].substring(lastTagStart))
        for (line in mStartLine until mEndLine - 1) {
            resultMas.add(splitContent[line])
        }
        resultMas.add(splitContent[mEndLine - 1].substring(0, mEndColumn))

        // Return multiline string built from created list elements
        return resultMas.joinToString("\n")
    }
}