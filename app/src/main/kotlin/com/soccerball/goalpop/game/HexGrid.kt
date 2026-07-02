package com.soccerball.goalpop.game

import androidx.compose.ui.geometry.Offset
import com.soccerball.goalpop.game.model.GridCell
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

class HexGrid(
    val cols: Int,
    val rows: Int,
    val ballRadius: Float,
    val offsetX: Float,
    val offsetY: Float,
) {
    val cellWidth: Float get() = ballRadius * 2f
    val rowHeight: Float get() = sqrt(3f) * ballRadius

    fun isOddRow(row: Int): Boolean = row % 2 == 1

    fun cellToPixel(row: Int, col: Int): Offset {
        val xOffset = if (isOddRow(row)) ballRadius else 0f
        val x = offsetX + col * cellWidth + xOffset + ballRadius
        val y = offsetY + row * rowHeight + ballRadius
        return Offset(x, y)
    }

    fun pixelToCell(x: Float, y: Float): Pair<Int, Int> {
        val row = ((y - offsetY - ballRadius) / rowHeight).roundToInt().coerceIn(0, rows - 1)
        val xOffset = if (isOddRow(row)) ballRadius else 0f
        val col = ((x - offsetX - xOffset - ballRadius) / cellWidth).roundToInt().coerceIn(0, cols - 1)
        return row to col
    }

    fun snapToNearestEmpty(
        x: Float,
        y: Float,
        occupied: Set<Pair<Int, Int>>,
    ): Pair<Int, Int>? {
        val candidates = mutableListOf<Pair<Int, Int>>()
        val (baseRow, baseCol) = pixelToCell(x, y)

        for (dr in -1..1) {
            for (dc in -1..1) {
                val r = baseRow + dr
                val c = baseCol + dc
                if (r in 0 until rows && c in 0 until cols && (r to c) !in occupied) {
                    candidates.add(r to c)
                }
            }
        }

        if (candidates.isEmpty()) {
            for (dr in -2..2) {
                for (dc in -2..2) {
                    val r = baseRow + dr
                    val c = baseCol + dc
                    if (r in 0 until rows && c in 0 until cols && (r to c) !in occupied) {
                        candidates.add(r to c)
                    }
                }
            }
        }

        return candidates.minByOrNull { (r, c) ->
            val pos = cellToPixel(r, c)
            val dx = pos.x - x
            val dy = pos.y - y
            dx * dx + dy * dy
        }
    }

    fun neighbors(row: Int, col: Int): List<Pair<Int, Int>> {
        val offsets = if (isOddRow(row)) {
            listOf(
                -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to 0, 1 to 1,
            )
        } else {
            listOf(
                -1 to -1, -1 to 0, 0 to -1, 0 to 1, 1 to -1, 1 to 0,
            )
        }
        return offsets.mapNotNull { (dr, dc) ->
            val r = row + dr
            val c = col + dc
            if (r in 0 until rows && c in 0 until cols) r to c else null
        }
    }

    fun isValidCell(row: Int, col: Int): Boolean =
        row in 0 until rows && col in 0 until cols

    fun distance(a: Pair<Int, Int>, b: Pair<Int, Int>): Float {
        val pa = cellToPixel(a.first, a.second)
        val pb = cellToPixel(b.first, b.second)
        val dx = pa.x - pb.x
        val dy = pa.y - pb.y
        return sqrt(dx * dx + dy * dy)
    }

    fun cellsNearPixel(x: Float, y: Float, maxDist: Float): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        val (centerRow, centerCol) = pixelToCell(x, y)
        for (dr in -2..2) {
            for (dc in -3..3) {
                val r = centerRow + dr
                val c = centerCol + dc
                if (!isValidCell(r, c)) continue
                val pos = cellToPixel(r, c)
                val dx = pos.x - x
                val dy = pos.y - y
                if (sqrt(dx * dx + dy * dy) <= maxDist) {
                    result.add(r to c)
                }
            }
        }
        return result
    }

    fun attachPosition(hitX: Float, hitY: Float, occupied: Set<Pair<Int, Int>>): Pair<Int, Int>? {
        val nearCells = cellsNearPixel(hitX, hitY, ballRadius * 2.2f)
        val emptyNeighbors = nearCells
            .filter { it !in occupied }
            .filter { cell ->
                neighbors(cell.first, cell.second).any { it in occupied } || cell.first == 0
            }

        return emptyNeighbors.minByOrNull { (r, c) ->
            val pos = cellToPixel(r, c)
            val dx = pos.x - hitX
            val dy = pos.y - hitY
            abs(dx) + abs(dy)
        } ?: snapToNearestEmpty(hitX, hitY, occupied)
    }
}
