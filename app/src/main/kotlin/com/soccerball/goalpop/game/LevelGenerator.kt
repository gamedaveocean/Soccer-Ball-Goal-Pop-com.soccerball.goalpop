package com.soccerball.goalpop.game

import com.soccerball.goalpop.game.model.GridCell
import kotlin.random.Random

object LevelGenerator {
    private const val MAX_LEVELS = 30
    private const val MAX_BALL_COLORS = 4

    fun levelConfig(level: Int): LevelConfig {
        val clamped = level.coerceIn(1, MAX_LEVELS)
        val colorCount = (4 + clamped / 6).coerceAtMost(MAX_BALL_COLORS)
        val rowCount = (5 + clamped / 3).coerceAtMost(9)
        val moves = (35 - clamped / 2).coerceAtLeast(20)
        return LevelConfig(
            level = clamped,
            colorCount = colorCount,
            rowCount = rowCount,
            moves = moves,
        )
    }

    fun generateGrid(config: LevelConfig, cols: Int): Map<Pair<Int, Int>, GridCell> {
        val grid = mutableMapOf<Pair<Int, Int>, GridCell>()
        val random = Random(config.level * 9973)

        for (row in 0 until config.rowCount) {
            for (col in 0 until cols) {
                if (random.nextFloat() < 0.08f) continue
                val colorIndex = random.nextInt(config.colorCount)
                grid[row to col] = GridCell(row, col, colorIndex)
            }
        }

        if (grid.size < 15) {
            for (row in 0 until config.rowCount) {
                for (col in 0 until cols) {
                    if ((row to col) !in grid && random.nextFloat() < 0.7f) {
                        grid[row to col] = GridCell(row, col, random.nextInt(config.colorCount))
                    }
                }
            }
        }

        ensureNoImmediateMatches(grid, cols, config.colorCount, random)
        return grid
    }

    private fun ensureNoImmediateMatches(
        grid: MutableMap<Pair<Int, Int>, GridCell>,
        cols: Int,
        colorCount: Int,
        random: Random,
    ) {
        val layout = HexGrid(cols, 14, 1f, 0f, 0f)
        var changed = true
        var iterations = 0
        while (changed && iterations < 50) {
            changed = false
            iterations++
            for ((pos, cell) in grid.toList()) {
                val cluster = MatchDetector.findCluster(pos, grid, layout, minSize = 3)
                if (cluster.isNotEmpty()) {
                    val newColor = (cell.colorIndex + 1 + random.nextInt(colorCount - 1)) % colorCount
                    grid[pos] = cell.copy(colorIndex = newColor)
                    changed = true
                }
            }
        }
    }

    fun randomBallColor(colorCount: Int, random: Random = Random.Default): Int =
        random.nextInt(colorCount)
}

data class LevelConfig(
    val level: Int,
    val colorCount: Int,
    val rowCount: Int,
    val moves: Int,
)
