package com.soccerball.goalpop.game

import androidx.compose.ui.geometry.Offset
import com.soccerball.goalpop.game.model.FallingBall
import com.soccerball.goalpop.game.model.FlyingBall
import com.soccerball.goalpop.game.model.GamePhase
import com.soccerball.goalpop.game.model.GameUiState
import com.soccerball.goalpop.game.model.GridCell
import kotlin.math.sqrt
import kotlin.random.Random

class BubbleShooterEngine(
    private val cols: Int = 11,
    private val rows: Int = 14,
) {
    companion object {
        const val MAX_MISTAKES = 5
    }

    private var colorCount = 4
    private var random: Random = Random.Default
    private var gridLayout: HexGrid? = null
    private var playLeft = 0f
    private var playRight = 0f
    private var playTop = 0f
    private var playBottom = 0f
    private var dangerLineY = 0f
    private var shooterPos = Offset.Zero
    private var launchPoint = Offset.Zero
    private var ballRadius = 0f

    fun configureLayout(
        width: Float,
        playfieldHeight: Float,
    ) {
        if (width <= 0f || playfieldHeight <= 0f) return

        val usableWidth = width * 0.92f
        ballRadius = usableWidth / (cols * 2f + 1f)
        val offsetX = (width - cols * ballRadius * 2f - ballRadius) / 2f
        val offsetY = ballRadius + 8f
        gridLayout = HexGrid(cols, rows, ballRadius, offsetX, offsetY)
        playLeft = offsetX
        playRight = width - offsetX
        playTop = offsetY
        playBottom = playfieldHeight
        launchPoint = Offset(width / 2f, playfieldHeight - ballRadius * 2.5f)
        shooterPos = launchPoint
        dangerLineY = launchPoint.y - ballRadius * 4.5f
    }

    fun launchPoint(): Offset = launchPoint

    fun playfieldBottom(): Float = playBottom

    fun canStartAim(touch: Offset): Boolean {
        if (ballRadius <= 0f) return false
        return isTouchOnShooter(touch) || touch.y < playBottom
    }

    fun dangerLineY(): Float = dangerLineY

    fun shooterPosition(): Offset = shooterPos

    fun ballRadius(): Float = ballRadius

    fun grid(): HexGrid = gridLayout ?: error("Layout not configured")

    fun isTouchOnShooter(touch: Offset): Boolean {
        if (shooterPos == Offset.Zero || ballRadius <= 0f) return false
        val dx = touch.x - shooterPos.x
        val dy = touch.y - shooterPos.y
        return sqrt(dx * dx + dy * dy) <= ballRadius * 2.8f
    }

    fun startLevel(level: Int): GameUiState {
        GridCell.resetIds()
        val config = LevelGenerator.levelConfig(level)
        colorCount = config.colorCount
        random = Random(level * 7919L)
        val grid = LevelGenerator.generateGrid(config, cols)
        val current = LevelGenerator.randomBallColor(colorCount, random)
        val next = LevelGenerator.randomBallColor(colorCount, random)
        return GameUiState(
            level = level,
            score = 0,
            mistakesLeft = MAX_MISTAKES,
            phase = GamePhase.Aiming,
            grid = grid,
            currentBallColor = current,
            nextBallColor = next,
            cols = cols,
            rows = rows,
        )
    }

    fun beginAim(state: GameUiState, touchPos: Offset): GameUiState {
        if (state.phase != GamePhase.Aiming || !canStartAim(touchPos)) return state
        val angle = ShotPhysics.angleFromLauncher(launchPoint, touchPos)
        return state.copy(isAiming = true, aimAngle = angle)
    }

    fun updateAim(state: GameUiState, touchPos: Offset): GameUiState {
        if (state.phase != GamePhase.Aiming || !state.isAiming) return state
        val angle = ShotPhysics.angleFromLauncher(launchPoint, touchPos)
        return state.copy(aimAngle = angle)
    }

    fun cancelAim(state: GameUiState): GameUiState =
        state.copy(isAiming = false, aimAngle = ShotPhysics.DEFAULT_ANGLE_DEG)

    fun shoot(state: GameUiState): GameUiState {
        if (state.phase != GamePhase.Aiming || !state.isAiming) return state
        val velocity = ShotPhysics.velocityFromAngle(state.aimAngle)
        val launchPos = launchPoint
        val flying = FlyingBall(
            position = launchPos,
            velocity = velocity,
            colorIndex = state.currentBallColor,
        )
        return state.copy(
            phase = GamePhase.Shooting,
            flyingBall = flying,
            isAiming = false,
        )
    }

    fun tickShooting(state: GameUiState, dt: Float): GameUiState {
        val flying = state.flyingBall ?: return state
        val layout = grid()
        val radius = layout.ballRadius
        var pos = flying.position
        var vel = flying.velocity

        pos = ShotPhysics.advance(pos, vel, dt)
        val reflected = ShotPhysics.reflectFromWall(pos, vel, playLeft + radius, playRight - radius, radius)
        pos = reflected.first
        vel = reflected.second

        if (pos.y - radius <= playTop) {
            return attachBall(state, pos.x, playTop + radius, flying.colorIndex)
        }

        for ((cellPos, _) in state.grid) {
            val cellCenter = layout.cellToPixel(cellPos.first, cellPos.second)
            val dx = pos.x - cellCenter.x
            val dy = pos.y - cellCenter.y
            val dist = sqrt(dx * dx + dy * dy)
            if (dist < radius * 2f - 2f) {
                return attachBall(state, pos.x, pos.y, flying.colorIndex)
            }
        }

        if (pos.y > playBottom + radius) {
            return attachBall(state, pos.x, playBottom - radius, flying.colorIndex)
        }

        return state.copy(flyingBall = flying.copy(position = pos, velocity = vel))
    }

    private fun attachBall(state: GameUiState, hitX: Float, hitY: Float, colorIndex: Int): GameUiState {
        val layout = grid()
        val occupied = state.grid.keys
        val attachCell = layout.attachPosition(hitX, hitY.coerceAtMost(playBottom), occupied)
            ?: layout.snapToNearestEmpty(hitX, hitY.coerceIn(playTop, playBottom), occupied)
            ?: return state.copy(
                phase = GamePhase.Aiming,
                flyingBall = null,
                isAiming = false,
            )

        val newGrid = state.grid.toMutableMap()
        newGrid[attachCell] = GridCell(attachCell.first, attachCell.second, colorIndex)

        val nextCurrent = state.nextBallColor
        val nextNext = LevelGenerator.randomBallColor(colorCount, random)

        val newState = state.copy(
            grid = newGrid,
            flyingBall = null,
            phase = GamePhase.Resolving,
            currentBallColor = nextCurrent,
            nextBallColor = nextNext,
            poppingCells = emptySet(),
            fallingBalls = emptyList(),
            isAiming = false,
        )

        return resolveMatches(newState, attachCell)
    }

    private fun resolveMatches(state: GameUiState, placed: Pair<Int, Int>): GameUiState {
        val layout = grid()
        var grid = state.grid.toMutableMap()
        var score = state.score
        var mistakes = state.mistakesLeft
        var popping = emptySet<Pair<Int, Int>>()

        val cluster = MatchDetector.findCluster(placed, grid, layout)
        if (cluster.isNotEmpty()) {
            popping = cluster
            score += cluster.size * 10
            cluster.forEach { grid.remove(it) }
        } else {
            mistakes -= 1
        }

        val floating = MatchDetector.findFloatingCells(grid, layout)
        val falling = floating.map { pos ->
            val cell = state.grid[pos]!!
            val pixel = layout.cellToPixel(pos.first, pos.second)
            FallingBall(cell, pixel, 0f)
        }
        floating.forEach { grid.remove(it) }
        score += floating.size * 20

        var phase = when {
            grid.isEmpty() -> GamePhase.Won
            mistakes <= 0 -> GamePhase.Lost
            isDangerReached(grid, layout) -> GamePhase.Lost
            else -> GamePhase.Resolving
        }

        if (cluster.isEmpty() && floating.isEmpty() && phase == GamePhase.Resolving) {
            phase = GamePhase.Aiming
        }

        return state.copy(
            grid = grid,
            score = score,
            mistakesLeft = mistakes,
            phase = phase,
            poppingCells = popping,
            fallingBalls = falling,
        )
    }

    private fun isDangerReached(grid: Map<Pair<Int, Int>, GridCell>, layout: HexGrid): Boolean {
        return grid.keys.any { (row, col) ->
            layout.cellToPixel(row, col).y >= dangerLineY
        }
    }

    fun tickFalling(state: GameUiState, dt: Float): GameUiState {
        if (state.fallingBalls.isEmpty()) {
            if (state.phase == GamePhase.Resolving) {
                val newPhase = when {
                    state.grid.isEmpty() -> GamePhase.Won
                    state.mistakesLeft <= 0 -> GamePhase.Lost
                    isDangerReached(state.grid, grid()) -> GamePhase.Lost
                    else -> GamePhase.Aiming
                }
                return state.copy(phase = newPhase, poppingCells = emptySet())
            }
            return state
        }

        val gravity = 600f
        val updated = state.fallingBalls.map { ball ->
            val newVel = ball.velocityY + gravity * dt
            ball.copy(
                position = Offset(ball.position.x, ball.position.y + newVel * dt),
                velocityY = newVel,
            )
        }.filter { it.position.y < playBottom + 200f }

        return state.copy(fallingBalls = updated)
    }

    fun tickPopping(state: GameUiState): GameUiState {
        if (state.poppingCells.isEmpty()) return state
        return state.copy(poppingCells = emptySet())
    }
}
