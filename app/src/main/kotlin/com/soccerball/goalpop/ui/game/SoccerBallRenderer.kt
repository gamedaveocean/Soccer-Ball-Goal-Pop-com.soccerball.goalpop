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
    const val columns = 2
    const val rows = 2
    const val uniqueColorCount: Int = 4
}

@Composable
fun rememberBallSprites(): ImageBitmap {
    val context = LocalContext.current
    return remember {
        ImageBitmap.imageResource(context.resources, R.drawable.ui_balls_sheet)
    }
}

fun DrawScope.drawSoccerBall(
    center: Offset,
    radius: Float,
    colorIndex: Int,
    sprites: ImageBitmap,
) {
    if (radius <= 0f) return
    val diameter = (radius * 2f).coerceAtLeast(1f)
    val topLeft = Offset(center.x - radius, center.y - radius)
    val cell = colorIndex % BallSprites.uniqueColorCount
    val cellW = sprites.width / BallSprites.columns
    val cellH = sprites.height / BallSprites.rows
    val col = cell % BallSprites.columns
    val row = cell / BallSprites.columns
    val clip = Path().apply {
        addOval(Rect(topLeft, Size(diameter, diameter)))
    }

    clipPath(clip) {
        drawImage(
            image = sprites,
            srcOffset = IntOffset(col * cellW, row * cellH),
            srcSize = IntSize(cellW, cellH),
            dstOffset = IntOffset(topLeft.x.toInt(), topLeft.y.toInt()),
            dstSize = IntSize(diameter.toInt().coerceAtLeast(1), diameter.toInt().coerceAtLeast(1)),
            filterQuality = FilterQuality.High,
        )
    }
}
