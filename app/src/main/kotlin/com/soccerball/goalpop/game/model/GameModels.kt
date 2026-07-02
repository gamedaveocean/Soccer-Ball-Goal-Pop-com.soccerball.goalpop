package com.soccerball.goalpop.game.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class GridCell(
    val row: Int,
    val col: Int,
    val colorIndex: Int,
    val id: Long = nextId(),
) {
    companion object {
        private var idCounter = 0L
        fun nextId(): Long = ++idCounter
        fun resetIds() {
            idCounter = 0L
        }
    }
}

data class FlyingBall(
    val position: Offset,
    val velocity: Offset,
    val colorIndex: Int,
)

data class FallingBall(
    val cell: GridCell,
    val position: Offset,
    val velocityY: Float,
)

enum class GamePhase {
    Aiming,
    Shooting,
    Resolving,
    Won,
    Lost,
}

data class GameUiState(
    val level: Int = 1,
    val score: Int = 0,
    val mistakesLeft: Int = 5,
    val phase: GamePhase = GamePhase.Aiming,
    val grid: Map<Pair<Int, Int>, GridCell> = emptyMap(),
    val currentBallColor: Int = 0,
    val nextBallColor: Int = 1,
    val flyingBall: FlyingBall? = null,
    val aimAngle: Float = -90f,
    val isAiming: Boolean = false,
    val fallingBalls: List<FallingBall> = emptyList(),
    val poppingCells: Set<Pair<Int, Int>> = emptySet(),
    val cols: Int = 11,
    val rows: Int = 14,
)

fun colorForIndex(index: Int, palette: List<Color>): Color =
    palette[index % palette.size]
