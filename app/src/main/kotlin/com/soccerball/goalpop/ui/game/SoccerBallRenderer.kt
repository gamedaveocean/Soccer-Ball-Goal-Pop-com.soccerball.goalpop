package com.soccerball.goalpop.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.soccerball.goalpop.R

object BallSprites {
    val drawableIds = intArrayOf(
        R.drawable.ball_classic,
        R.drawable.ball_blue,
        R.drawable.ball_red,
        R.drawable.ball_yellow,
        R.drawable.ball_green,
        R.drawable.ball_orange,
    )

    const val uniqueColorCount: Int = 6
}

@Composable
fun rememberBallSprites(): List<ImageBitmap> {
    val context = LocalContext.current
    return remember {
        BallSprites.drawableIds.map { id ->
            ImageBitmap.imageResource(context.resources, id)
        }
    }
}

fun DrawScope.drawSoccerBall(
    center: Offset,
    radius: Float,
    colorIndex: Int,
    sprites: List<ImageBitmap>,
) {
    if (radius <= 0f || sprites.isEmpty()) return
    val image = sprites[colorIndex % sprites.size]
    val diameter = (radius * 2f).coerceAtLeast(1f)
    val topLeft = Offset(center.x - radius, center.y - radius)
    val clip = Path().apply {
        addOval(Rect(topLeft, Size(diameter, diameter)))
    }

    clipPath(clip) {
        drawImage(
            image = image,
            dstOffset = IntOffset(topLeft.x.toInt(), topLeft.y.toInt()),
            dstSize = IntSize(diameter.toInt().coerceAtLeast(1), diameter.toInt().coerceAtLeast(1)),
            filterQuality = FilterQuality.High,
        )
    }
}
