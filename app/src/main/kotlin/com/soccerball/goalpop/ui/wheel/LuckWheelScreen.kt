package com.soccerball.goalpop.ui.wheel

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import kotlinx.coroutines.launch
import kotlin.random.Random

private val wheelPrizes = listOf(100, 250, 500, 750, 1000, 2000)
private val wheelColors = listOf(
    Color(0xFFE53935),
    Color(0xFFFB8C00),
    Color(0xFFFFC107),
    Color(0xFF43A047),
    Color(0xFF1E88E5),
    Color(0xFF8E24AA),
)

private enum class LuckWheelPhase {
    Ready,
    Spinning,
    Result,
    Claimed,
}

@Composable
fun LuckWheelScreen(
    preferences: GamePreferences,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val rewardedAds = remember { RewardedAdManager.getInstance(app) }
    val coins by preferences.coins.collectAsState(initial = 0)
    val scope = rememberCoroutineScope()
    val rotation = rememberWheelRotation()

    var phase by remember { mutableStateOf(LuckWheelPhase.Ready) }
    var prizeIndex by remember { mutableIntStateOf(0) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val segments = remember {
        wheelPrizes.map { prize -> "${prize}" }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SkyBlue, FieldGreen, FieldDarkGreen)))
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.lucky_wheel),
            style = MaterialTheme.typography.displayLarge,
            color = UiGold,
        )
        Text(
            text = "${stringResource(R.string.coins)}: $coins",
            style = MaterialTheme.typography.titleLarge,
            color = GoalWhite,
        )
        Spacer(modifier = Modifier.height(24.dp))

        BoxWithConstraints {
            val wheelSize = (maxWidth * 0.78f).coerceIn(220.dp, 320.dp)
            Box(
                modifier = Modifier
                    .size(wheelSize)
                    .then(
                        if (phase == LuckWheelPhase.Ready) {
                            Modifier
                                .pulseScale()
                                .clickable(enabled = true) {
                                    phase = LuckWheelPhase.Spinning
                                    prizeIndex = Random.nextInt(wheelPrizes.size)
                                    scope.launch {
                                        spinWheelToIndex(
                                            animatable = rotation,
                                            targetIndex = prizeIndex,
                                            segmentCount = wheelPrizes.size,
                                            durationMillis = 2200,
                                        )
                                        phase = LuckWheelPhase.Result
                                    }
                                }
                        } else {
                            Modifier
                        },
                    ),
                contentAlignment = Alignment.TopCenter,
            ) {
                RewardWheel(
                    segments = segments,
                    segmentColors = wheelColors,
                    rotationDegrees = rotation.value,
                    modifier = Modifier.fillMaxSize(),
                    centerLabel = when (phase) {
                        LuckWheelPhase.Result, LuckWheelPhase.Claimed -> "${wheelPrizes[prizeIndex]}"
                        else -> null
                    },
                )
                WheelPointer()
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (phase) {
            LuckWheelPhase.Ready -> {
                Text(
                    text = stringResource(R.string.tap),
                    style = MaterialTheme.typography.headlineMedium,
                    color = UiGold,
                    fontWeight = FontWeight.Bold,
                )
            }
            LuckWheelPhase.Spinning -> {
                Text(
                    text = stringResource(R.string.spinning),
                    style = MaterialTheme.typography.titleLarge,
                    color = GoalWhite,
                )
            }
            LuckWheelPhase.Result -> {
                Text(
                    text = stringResource(R.string.you_won_coins, wheelPrizes[prizeIndex]),
                    style = MaterialTheme.typography.titleLarge,
                    color = GoalWhite,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        statusMessage = null
                        val prize = wheelPrizes[prizeIndex]
                        val itemId = Analytics.itemLuckyWheel(prize)
                        Analytics.reportPurchaseClick(itemId, Analytics.TYPE_COIN)
                        rewardedAds.showRewarded(
                            onReward = {
                                scope.launch {
                                    preferences.addCoins(prize)
                                }
                                Analytics.reportPurchaseSuccess(itemId, 0.0, Analytics.TYPE_COIN)
                                phase = LuckWheelPhase.Claimed
                                statusMessage = context.getString(R.string.coins_added, prize)
                            },
                            onFailed = {
                                Analytics.reportPurchaseError(itemId, Analytics.TYPE_COIN)
                                statusMessage = context.getString(R.string.ad_not_ready)
                            },
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .pulseScale(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UiGold,
                        contentColor = UiDark,
                    ),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_video_reward),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = stringResource(R.string.watch_and_claim),
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
                    )
                }
            }
            LuckWheelPhase.Claimed -> {
                Text(
                    text = statusMessage ?: stringResource(R.string.reward_claimed),
                    style = MaterialTheme.typography.titleLarge,
                    color = UiGold,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = {
                        phase = LuckWheelPhase.Ready
                        statusMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(0.8f),
                ) {
                    Text(stringResource(R.string.spin_again), color = GoalWhite)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .navigationBarsPadding(),
        ) {
            Text(stringResource(R.string.back), color = GoalWhite)
        }
    }
}
