package com.soccerball.goalpop.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RewardWheel(
    segments: List<String>,
    segmentColors: List<Color>,
    rotationDegrees: Float,
    modifier: Modifier = Modifier,
    centerLabel: String? = null,
) {
    val safeColors = if (segmentColors.size >= segments.size) {
        segmentColors
    } else {
        List(segments.size) { index -> segmentColors[index % segmentColors.size] }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val slice = 360f / segments.size
            val radius = size.minDimension / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            rotate(rotationDegrees, center) {
                segments.forEachIndexed { index, label ->
                    val start = index * slice - 90f
                    drawArc(
                        color = safeColors[index],
                        startAngle = start,
                        sweepAngle = slice,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                    )

                    val angleRad = Math.toRadians((start + slice / 2).toDouble())
                    val textRadius = radius * 0.62f
                    val tx = center.x + cos(angleRad).toFloat() * textRadius
                    val ty = center.y + sin(angleRad).toFloat() * textRadius

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = radius * 0.18f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                        drawText(label, tx, ty + paint.textSize / 3f, paint)
                    }
                }
            }

            drawCircle(color = Color.White, radius = radius * 0.18f, center = center)
            drawCircle(color = Color(0xFF1B5E20), radius = radius * 0.14f, center = center)
        }

        centerLabel?.let { label ->
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

suspend fun spinWheelToIndex(
    animatable: Animatable<Float, *>,
    targetIndex: Int,
    segmentCount: Int,
    extraSpins: Int = 4,
    durationMillis: Int = 1500,
): Float {
    val slice = 360f / segmentCount
    val targetAngle = 360f * extraSpins + (segmentCount - targetIndex) * slice - slice / 2f
    animatable.animateTo(
        targetValue = animatable.value + targetAngle,
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
    )
    return animatable.value % 360f
}

@Composable
fun rememberWheelRotation(): Animatable<Float, *> = remember { Animatable(0f) }

@Composable
fun WheelPointer(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(28.dp)) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width / 2f, 0f)
            lineTo(0f, size.height)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(path, color = Color(0xFFFFC107))
    }
}
