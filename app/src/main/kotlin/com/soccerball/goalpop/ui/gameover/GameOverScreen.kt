package com.soccerball.goalpop.ui.gameover

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.soccerball.goalpop.R
import com.soccerball.goalpop.ads.RewardedAdManager
import com.soccerball.goalpop.analytics.Analytics
import com.soccerball.goalpop.data.GamePreferences
import com.soccerball.goalpop.ui.components.RewardWheel
import com.soccerball.goalpop.ui.components.WheelPointer
import com.soccerball.goalpop.ui.components.pulseScale
import com.soccerball.goalpop.ui.components.rememberWheelRotation
import com.soccerball.goalpop.ui.components.spinWheelToIndex
import com.soccerball.goalpop.ui.theme.FieldDarkGreen
import com.soccerball.goalpop.ui.theme.FieldGreen
import com.soccerball.goalpop.ui.theme.GoalWhite
import com.soccerball.goalpop.ui.theme.SkyBlue
import com.soccerball.goalpop.ui.theme.UiDark
import com.soccerball.goalpop.ui.theme.UiGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private val boostMultipliers = (1..10).map { multiplier -> "x$multiplier" }
private val boostColors = listOf(
    Color(0xFFE53935),
    Color(0xFFFB8C00),
    Color(0xFFFFC107),
    Color(0xFF7CFC00),
    Color(0xFF43A047),
    Color(0xFF1E88E5),
    Color(0xFF5E35B1),
    Color(0xFF8E24AA),
    Color(0xFFD81B60),
    Color(0xFF00ACC1),
)

private enum class WinBoostPhase {
    OfferBoost,
    Spinning,
    Claim,
    Done,
}

@Composable
fun GameOverScreen(
    preferences: GamePreferences,
    score: Int,
    won: Boolean,
    level: Int,
    onRetry: () -> Unit,
    onMenu: () -> Unit,
) {
    val highScore by preferences.highScore.collectAsState(initial = 0)
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val rewardedAds = remember { RewardedAdManager.getInstance(app) }
    val scope = rememberCoroutineScope()
    val rotation = rememberWheelRotation()

    var winPhase by remember { mutableStateOf(WinBoostPhase.OfferBoost) }
    var multiplierIndex by remember { mutableIntStateOf(0) }
    var showBoostButton by remember { mutableStateOf(false) }
    var baseRewardGranted by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val baseWin = score.coerceAtLeast(10)
    val boostedReward = baseWin * (multiplierIndex + 1)

    LaunchedEffect(won) {
        if (won) {
            delay(2_000)
            showBoostButton = true
        }
    }

    fun grantBaseIfNeeded() {
        if (!won || baseRewardGranted) return
        scope.launch {
            preferences.addCoins(baseWin)
            baseRewardGranted = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SkyBlue, FieldGreen, FieldDarkGreen))),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (won) {
                WinScreenContent(
                    baseWin = baseWin,
                    winPhase = winPhase,
                    showBoostButton = showBoostButton,
                    rotation = rotation,
                    multiplierIndex = multiplierIndex,
                    boostedReward = boostedReward,
                    statusMessage = statusMessage,
                    onBoostClick = {
                        winPhase = WinBoostPhase.Spinning
                        multiplierIndex = Random.nextInt(boostMultipliers.size)
                        scope.launch {
                            spinWheelToIndex(
                                animatable = rotation,
                                targetIndex = multiplierIndex,
                                segmentCount = boostMultipliers.size,
                                durationMillis = 1500,
                            )
                            winPhase = WinBoostPhase.Claim
                        }
                    },
                    onClaimClick = {
                        statusMessage = null
                        val itemId = Analytics.itemWinBoost(multiplierIndex + 1)
                        Analytics.reportPurchaseClick(itemId, Analytics.TYPE_COIN)
                        rewardedAds.showRewarded(
                            onReward = {
                                scope.launch {
                                    preferences.addCoins(boostedReward)
                                    baseRewardGranted = true
                                }
                                Analytics.reportPurchaseSuccess(itemId, 0.0, Analytics.TYPE_COIN)
                                winPhase = WinBoostPhase.Done
                                statusMessage = context.getString(R.string.coins_added, boostedReward)
                            },
                            onFailed = {
                                Analytics.reportPurchaseError(itemId, Analytics.TYPE_COIN)
                                statusMessage = context.getString(R.string.ad_not_ready)
                            },
                        )
                    },
                )
            } else {
                Text(
                    text = stringResource(R.string.game_over),
                    style = MaterialTheme.typography.displayLarge,
                    color = GoalWhite,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "${stringResource(R.string.score)}: $score",
                    style = MaterialTheme.typography.headlineMedium,
                    color = GoalWhite,
                )
            }

            if (!won || winPhase == WinBoostPhase.Done) {
                Text(
                    text = "${stringResource(R.string.best_score)}: $highScore",
                    style = MaterialTheme.typography.titleLarge,
                    color = GoalWhite.copy(alpha = 0.8f),
                )
                if (won) {
                    Text(
                        text = "${stringResource(R.string.level)} ${level + 1}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = UiGold,
                    )
                }
                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        if (won && !baseRewardGranted && winPhase != WinBoostPhase.Done) {
                            grantBaseIfNeeded()
                        }
                        onRetry()
                    },
                    modifier = Modifier.fillMaxWidth(0.7f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UiGold,
                        contentColor = UiDark,
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.retry),
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        if (won && !baseRewardGranted && winPhase != WinBoostPhase.Done) {
                            grantBaseIfNeeded()
                        }
                        onMenu()
                    },
                    modifier = Modifier.fillMaxWidth(0.7f),
                ) {
                    Text(
                        text = stringResource(R.string.menu),
                        color = GoalWhite,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun WinScreenContent(
    baseWin: Int,
    winPhase: WinBoostPhase,
    showBoostButton: Boolean,
    rotation: androidx.compose.animation.core.Animatable<Float, *>,
    multiplierIndex: Int,
    boostedReward: Int,
    statusMessage: String?,
    onBoostClick: () -> Unit,
    onClaimClick: () -> Unit,
) {
    Text(
        text = stringResource(R.string.you_win),
        style = MaterialTheme.typography.displayLarge,
        color = UiGold,
        textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(R.string.win_amount, baseWin),
        style = MaterialTheme.typography.headlineMedium,
        color = GoalWhite,
        fontWeight = FontWeight.Bold,
    )

    Spacer(modifier = Modifier.height(24.dp))

    when (winPhase) {
        WinBoostPhase.OfferBoost -> {
            if (showBoostButton) {
                Button(
                    onClick = onBoostClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .pulseScale(minScale = 0.94f, maxScale = 1.12f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6D00),
                        contentColor = GoalWhite,
                    ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_video_reward),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = GoalWhite,
                        )
                        Text(
                            text = stringResource(R.string.boost_reward),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 10.dp),
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.preparing_boost),
                    style = MaterialTheme.typography.bodyLarge,
                    color = GoalWhite.copy(alpha = 0.8f),
                )
            }
        }
        WinBoostPhase.Spinning, WinBoostPhase.Claim -> {
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.TopCenter,
            ) {
                RewardWheel(
                    segments = boostMultipliers,
                    segmentColors = boostColors,
                    rotationDegrees = rotation.value,
                    modifier = Modifier.fillMaxSize(),
                    centerLabel = if (winPhase == WinBoostPhase.Claim) {
                        "$boostedReward"
                    } else {
                        null
                    },
                )
                WheelPointer()
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (winPhase == WinBoostPhase.Spinning) {
                Text(
                    text = stringResource(R.string.spinning),
                    color = GoalWhite,
                    style = MaterialTheme.typography.titleMedium,
                )
            } else {
                Text(
                    text = stringResource(R.string.boost_result, boostMultipliers[multiplierIndex], boostedReward),
                    color = GoalWhite,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onClaimClick,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .pulseScale(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UiGold,
                        contentColor = UiDark,
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.claim),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
            }
        }
        WinBoostPhase.Done -> {
            Text(
                text = statusMessage ?: stringResource(R.string.reward_claimed),
                style = MaterialTheme.typography.titleLarge,
                color = UiGold,
                textAlign = TextAlign.Center,
            )
        }
    }
}
