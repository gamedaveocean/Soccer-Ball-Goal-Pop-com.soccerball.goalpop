package com.soccerball.goalpop.game

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object ShotPhysics {
    /**
     * Screen-space aim angles via atan2(dy, dx) where Y grows downward.
     * Upward shots are negative: -90 = straight up, -160 = up-left, -20 = up-right.
     */
    const val MIN_ANGLE_DEG = -160f
    const val MAX_ANGLE_DEG = -20f
    const val DEFAULT_ANGLE_DEG = -90f
    const val BALL_SPEED = 1100f

    fun clampAngle(angleDeg: Float): Float =
        angleDeg.coerceIn(MIN_ANGLE_DEG, MAX_ANGLE_DEG)

    fun angleFromLauncher(launcher: Offset, touch: Offset): Float {
        val dx = touch.x - launcher.x
        var dy = touch.y - launcher.y
        if (dy > -4f) dy = -4f
        val angleDeg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
        return clampAngle(angleDeg)
    }

    fun velocityFromAngle(angleDeg: Float): Offset {
        val rad = angleDeg * PI.toFloat() / 180f
        return Offset(cos(rad) * BALL_SPEED, sin(rad) * BALL_SPEED)
    }

    fun reflectFromWall(
        position: Offset,
        velocity: Offset,
        leftWall: Float,
        rightWall: Float,
        radius: Float,
    ): Pair<Offset, Offset> {
        var pos = position
        var vel = velocity
        if (pos.x - radius <= leftWall && vel.x < 0) {
            pos = pos.copy(x = leftWall + radius)
            vel = vel.copy(x = -vel.x)
        } else if (pos.x + radius >= rightWall && vel.x > 0) {
            pos = pos.copy(x = rightWall - radius)
            vel = vel.copy(x = -vel.x)
        }
        return pos to vel
    }

    fun advance(position: Offset, velocity: Offset, dt: Float): Offset =
        Offset(position.x + velocity.x * dt, position.y + velocity.y * dt)

    fun trajectoryPoints(
        start: Offset,
        angleDeg: Float,
        leftWall: Float,
        rightWall: Float,
        radius: Float,
        maxBounces: Int = 4,
        steps: Int = 60,
    ): List<Offset> {
        val points = mutableListOf<Offset>()
        var pos = start
        var vel = velocityFromAngle(angleDeg)
        var bounces = 0
        val dt = 0.016f

        repeat(steps) {
            pos = advance(pos, vel, dt)
            if (pos.x - radius <= leftWall || pos.x + radius >= rightWall) {
                if (bounces < maxBounces) {
                    val reflected = reflectFromWall(pos, vel, leftWall, rightWall, radius)
                    pos = reflected.first
                    vel = reflected.second
                    bounces++
                }
            }
            points.add(pos)
            if (pos.y < 0) return@repeat
        }
        return points
    }
}
