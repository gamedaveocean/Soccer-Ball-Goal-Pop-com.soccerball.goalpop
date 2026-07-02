package com.soccerball.goalpop.ui.game



import androidx.activity.compose.BackHandler

import androidx.compose.foundation.Image

import androidx.compose.foundation.Canvas

import androidx.compose.foundation.background

import androidx.compose.foundation.gestures.awaitEachGesture

import androidx.compose.foundation.gestures.awaitFirstDown

import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.BoxWithConstraints

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.displayCutoutPadding

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.navigationBarsPadding

import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.layout.size

import androidx.compose.foundation.layout.statusBarsPadding

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text

import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable

import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.collectAsState

import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableIntStateOf

import androidx.compose.runtime.remember

import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke

import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.layout.onSizeChanged

import androidx.compose.ui.res.painterResource

import androidx.compose.ui.res.stringResource

import androidx.compose.ui.unit.IntSize

import androidx.compose.ui.unit.dp

import com.soccerball.goalpop.R

import com.soccerball.goalpop.game.GameViewModel

import com.soccerball.goalpop.game.ShotPhysics

import com.soccerball.goalpop.game.model.GamePhase

import com.soccerball.goalpop.ui.theme.GoalWhite

import com.soccerball.goalpop.ui.theme.IconNeonGreen

import com.soccerball.goalpop.ui.theme.UiGold



@Composable

fun GameScreen(

    viewModel: GameViewModel,

    level: Int,

    onWin: (score: Int) -> Unit,

    onLose: (score: Int) -> Unit,

    onMainMenu: () -> Unit,

) {

    val ballSprites = rememberBallSprites()

    remember(level) {

        viewModel.startGame(level)

    }



    val state by viewModel.uiState.collectAsState()

    val isPaused by viewModel.isPaused.collectAsState()

    var endedPhase by remember(level) { mutableIntStateOf(0) }



    BackHandler {

        if (isPaused) {

            viewModel.resume()

        } else {

            viewModel.pause()

        }

    }



    if (isPaused) {

        PauseDialog(

            onResume = { viewModel.resume() },

            onMainMenu = onMainMenu,

        )

    }



    LaunchedEffect(state.phase) {

        when (state.phase) {

            GamePhase.Won -> {

                if (endedPhase != 1) {

                    endedPhase = 1

                    onWin(state.score)

                }

            }

            GamePhase.Lost -> {

                if (endedPhase != 2) {

                    endedPhase = 2

                    onLose(state.score)

                }

            }

            else -> Unit

        }

    }



    Box(

        modifier = Modifier

            .fillMaxSize()

            .background(Color(0xFF0A1628)),

    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            GameHud(

                level = state.level,

                score = state.score,

                mistakesLeft = state.mistakesLeft,

                nextColorIndex = state.nextBallColor,

                ballSprites = ballSprites,

                onPause = { viewModel.pause() },

            )



            BoxWithConstraints(

                modifier = Modifier

                    .weight(1f)

                    .fillMaxWidth()

                    .navigationBarsPadding(),

            ) {

                val totalWidth = constraints.maxWidth.toFloat()

                val totalHeight = constraints.maxHeight.toFloat()



                LaunchedEffect(totalWidth, totalHeight) {

                    viewModel.configureLayout(totalWidth, totalHeight)

                }



                Box(

                    modifier = Modifier

                        .fillMaxSize()

                        .onSizeChanged { size: IntSize ->

                            viewModel.configureLayout(

                                size.width.toFloat(),

                                size.height.toFloat(),

                            )

                        },

                ) {

                    Image(

                        painter = painterResource(R.drawable.game_stadium_bg),

                        contentDescription = null,

                        contentScale = ContentScale.Crop,

                        modifier = Modifier.fillMaxSize(),

                    )

                    GameCanvas(

                        viewModel = viewModel,

                        state = state,

                        showAimLine = state.isAiming,

                        ballSprites = ballSprites,

                    )

                }



                Box(

                    modifier = Modifier

                        .matchParentSize()

                        .pointerInput(level, state.phase, isPaused) {

                            awaitEachGesture {

                                if (viewModel.isPaused.value) return@awaitEachGesture

                                val down = awaitFirstDown(requireUnconsumed = false)

                                if (viewModel.uiState.value.phase != GamePhase.Aiming) return@awaitEachGesture

                                if (!viewModel.canStartAim(down.position)) return@awaitEachGesture



                                viewModel.onAimStart(down.position)

                                var released = false



                                do {

                                    val event = awaitPointerEvent()

                                    val change = event.changes.firstOrNull { it.id == down.id } ?: break



                                    if (change.pressed) {

                                        change.consume()

                                        viewModel.onAim(change.position)

                                    }



                                    if (!change.pressed) {

                                        released = true

                                        break

                                    }

                                } while (event.changes.any { it.pressed })



                                if (released && viewModel.uiState.value.isAiming) {

                                    viewModel.onShoot()

                                } else {

                                    viewModel.onAimCancel()

                                }

                            }

                        },

                )



                if (state.phase == GamePhase.Aiming && !state.isAiming) {

                    Text(

                        text = "Drag on field to aim · release to shoot",

                        color = GoalWhite.copy(alpha = 0.65f),

                        style = MaterialTheme.typography.labelLarge,

                        modifier = Modifier

                            .align(Alignment.BottomCenter)

                            .padding(bottom = 12.dp),

                    )

                }

            }

        }

    }

}



@Composable

private fun GameHud(

    level: Int,

    score: Int,

    mistakesLeft: Int,

    nextColorIndex: Int,

    ballSprites: List<ImageBitmap>,

    onPause: () -> Unit,

) {

    Row(

        modifier = Modifier

            .fillMaxWidth()

            .statusBarsPadding()

            .displayCutoutPadding()

            .background(Color(0xE6000000))

            .padding(horizontal = 12.dp, vertical = 6.dp),

        verticalAlignment = Alignment.CenterVertically,

    ) {

        Text(

            text = "${stringResource(R.string.level)} $level",

            color = GoalWhite,

            style = MaterialTheme.typography.titleMedium,

            modifier = Modifier.weight(1f),

        )

        Text(

            text = "${stringResource(R.string.score)}: $score",

            color = UiGold,

            style = MaterialTheme.typography.titleMedium,

            modifier = Modifier.weight(1f),

        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(

                text = stringResource(R.string.next_ball),

                color = GoalWhite.copy(alpha = 0.7f),

                style = MaterialTheme.typography.labelSmall,

            )

            Canvas(modifier = Modifier.size(22.dp)) {

                drawSoccerBall(

                    center = Offset(size.width / 2f, size.height / 2f),

                    radius = size.minDimension / 2f - 2f,

                    colorIndex = nextColorIndex,

                    sprites = ballSprites,

                )

            }

        }

        Text(

            text = "${stringResource(R.string.mistakes)}: $mistakesLeft",

            color = if (mistakesLeft <= 2) UiGold else GoalWhite,

            style = MaterialTheme.typography.bodyMedium,

            modifier = Modifier.padding(start = 8.dp),

        )

        TextButton(

            onClick = onPause,

            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),

            modifier = Modifier.padding(start = 4.dp),

        ) {

            Text(

                text = stringResource(R.string.pause),

                color = GoalWhite,

                style = MaterialTheme.typography.labelLarge,

            )

        }

    }

}



@Composable

private fun GameCanvas(

    viewModel: GameViewModel,

    state: com.soccerball.goalpop.game.model.GameUiState,

    showAimLine: Boolean,

    ballSprites: List<ImageBitmap>,

) {

    Canvas(modifier = Modifier.fillMaxSize()) {

        val layout = try {

            viewModel.grid()

        } catch (_: Exception) {

            return@Canvas

        }

        val radius = layout.ballRadius

        val leftWall = layout.offsetX

        val rightWall = size.width - layout.offsetX

        val dangerY = try {

            viewModel.dangerLineY()

        } catch (_: Exception) {

            size.height * 0.82f

        }

        val launch = try {

            viewModel.launchPoint()

        } catch (_: Exception) {

            Offset(size.width / 2f, size.height - radius * 2f)

        }



        drawGoal(layout.offsetX, layout.offsetY - radius, size.width - layout.offsetX * 2)



        state.grid.forEach { (pos, cell) ->

            if (pos in state.poppingCells) return@forEach

            drawSoccerBall(

                layout.cellToPixel(pos.first, pos.second),

                radius,

                cell.colorIndex,

                ballSprites,

            )

        }



        state.fallingBalls.forEach { ball ->

            drawSoccerBall(ball.position, radius, ball.cell.colorIndex, ballSprites)

        }



        state.poppingCells.forEach { pos ->

            val center = layout.cellToPixel(pos.first, pos.second)

            drawCircle(

                color = GoalWhite.copy(alpha = 0.6f),

                radius = radius * 1.2f,

                center = center,

            )

        }



        state.flyingBall?.let { flying ->

            drawSoccerBall(flying.position, radius, flying.colorIndex, ballSprites)

        }



        if (state.flyingBall == null && state.phase == GamePhase.Aiming) {

            drawSoccerBall(launch, radius, state.currentBallColor, ballSprites)

        }



        if (showAimLine) {

            val trajectory = ShotPhysics.trajectoryPoints(

                start = launch,

                angleDeg = state.aimAngle,

                leftWall = leftWall,

                rightWall = rightWall,

                radius = radius,

            )

            for (i in 0 until trajectory.size - 1 step 2) {

                drawLine(

                    color = GoalWhite.copy(alpha = 0.9f),

                    start = trajectory[i],

                    end = trajectory[i + 1],

                    strokeWidth = 4f,

                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(

                        floatArrayOf(14f, 10f),

                    ),

                )

            }

            drawCircle(

                color = GoalWhite.copy(alpha = 0.9f),

                radius = 8f,

                center = launch,

            )

        }



        drawLine(

            color = IconNeonGreen.copy(alpha = 0.7f),

            start = Offset(leftWall, dangerY),

            end = Offset(rightWall, dangerY),

            strokeWidth = 3f,

        )

    }

}



private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGoal(x: Float, y: Float, width: Float) {

    val goalHeight = 28f

    drawRect(

        color = GoalWhite.copy(alpha = 0.12f),

        topLeft = Offset(x, y),

        size = androidx.compose.ui.geometry.Size(width, goalHeight),

        style = Stroke(width = 2f),

    )

}


