package com.soccerball.goalpop.game

import com.soccerball.goalpop.game.model.GridCell

object MatchDetector {
    fun findCluster(
        start: Pair<Int, Int>,
        grid: Map<Pair<Int, Int>, GridCell>,
        gridLayout: HexGrid,
        minSize: Int = 3,
    ): Set<Pair<Int, Int>> {
        val startCell = grid[start] ?: return emptySet()
        val color = startCell.colorIndex
        val visited = mutableSetOf<Pair<Int, Int>>()
        val queue = ArrayDeque<Pair<Int, Int>>()
        queue.add(start)
        visited.add(start)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            for (neighbor in gridLayout.neighbors(current.first, current.second)) {
                if (neighbor in visited) continue
                val cell = grid[neighbor] ?: continue
                if (cell.colorIndex == color) {
                    visited.add(neighbor)
                    queue.add(neighbor)
                }
            }
        }

        return if (visited.size >= minSize) visited else emptySet()
    }

    fun findFloatingCells(
        grid: Map<Pair<Int, Int>, GridCell>,
        gridLayout: HexGrid,
    ): Set<Pair<Int, Int>> {
        if (grid.isEmpty()) return emptySet()

        val anchored = mutableSetOf<Pair<Int, Int>>()
        val queue = ArrayDeque<Pair<Int, Int>>()

        grid.keys.filter { it.first == 0 }.forEach {
            queue.add(it)
            anchored.add(it)
        }

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            for (neighbor in gridLayout.neighbors(current.first, current.second)) {
                if (neighbor in anchored || neighbor !in grid) continue
                anchored.add(neighbor)
                queue.add(neighbor)
            }
        }

        return grid.keys - anchored
    }
}
