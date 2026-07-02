package com.soccerball.goalpop.ui.wheel

import android.app.Application
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soccerball.goalpop.R
import com.soccerball.goalpop.ads.RewardedAdManager
import com.soccerball.goalpop.analytics.Analytics
import com.soccerball.goalpop.data.GamePreferences
import com.soccerball.goalpop.ui.components.ReferenceBackButton
import com.soccerball.goalpop.ui.components.ReferenceGreenButton
import com.soccerball.goalpop.ui.components.ReferenceScreenTitle
import com.soccerball.goalpop.ui.components.ReferenceWheelImage
import com.soccerball.goalpop.ui.components.StadiumBackground
import com.soccerball.goalpop.ui.components.rememberWheelRotation
import com.soccerball.goalpop.ui.components.spinWheelToIndex
import com.soccerball.goalpop.ui.dialogs.PleaseWaitOverlay
import com.soccerball.goalpop.ui.theme.GoalWhite
import com.soccerball.goalpop.ui.theme.IconNeonGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private data class WheelSegment(val label: String, val coins: Int, val freeSpins: Int = 0)

private val wheelSegments = listOf(
    WheelSegment("10000", 10000),
    WheelSegment("100", 100),
    WheelSegment("FAIL", 0),
    WheelSegment("500", 500),
    WheelSegment("1000", 1000),
    WheelSegment("200", 200),
    WheelSegment("3 SPINS", 0, freeSpins = 3),
    WheelSegment("300", 300),
    WheelSegment("FAIL", 0),
    WheelSegment("5000", 5000),
    WheelSegment("150", 150),
    WheelSegment("800", 800),
)

@Composable
fun LuckWheelScreen(
    preferences: GamePreferences,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val rewardedAds = remember { RewardedAdManager.getInstance(app) }
    val coins by preferences.coins.collectAsState(initial = 0)
    val freeSpins by preferences.freeSpins.collectAsState(initial = 0)
    val scope = rememberCoroutineScope()
    val rotation = rememberWheelRotation()

    var spinning by remember { mutableStateOf(false) }
    var prizeIndex by remember { mutableIntStateOf(0) }
    var canFreeSpin by remember { mutableStateOf(true) }
    var cooldownMs by remember { mutableLongStateOf(0L) }
    var showPleaseWait by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        canFreeSpin = preferences.canFreeWheelSpin()
    }

    LaunchedEffect(canFreeSpin, spinning) {
        while (!canFreeSpin && !spinning) {
            cooldownMs = GamePreferences.WHEEL_FREE_SPIN_COOLDOWN_MS
            delay(1000)
            canFreeSpin = preferences.canFreeWheelSpin()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StadiumBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ReferenceBackButton(onClick = onBack)
                Spacer(modifier = Modifier.width(12.dp))
                ReferenceScreenTitle(text = stringResource(R.string.wheel_of_luck))
            }

            Text(
                text = "${stringResource(R.string.coins)}: $coins",
                color = GoalWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                ReferenceWheelImage(
                    rotationDegrees = rotation.value,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(340.dp),
                )
            }

            statusMessage?.let { msg ->
                Text(
                    text = msg,
                    color = IconNeonGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            ReferenceGreenButton(
                text = when {
                    spinning -> stringResource(R.string.spinning)
                    canFreeSpin || freeSpins > 0 -> stringResource(R.string.spin)
                    else -> stringResource(R.string.free_spin)
                },
                onClick = {
                    if (spinning) return@ReferenceGreenButton
                    scope.launch {
                        val useFree = preferences.useFreeSpin()
                        val isFree = canFreeSpin || useFree
                        if (!isFree) {
                            showPleaseWait = true
                            rewardedAds.showRewarded(
                                onReward = {
                                    showPleaseWait = false
                                    performSpin(
                                        scope, rotation, preferences,
                                        onSpinning = { spinning = it },
                                        onPrize = { index, message ->
                                            prizeIndex = index
                                            statusMessage = message
                                            spinning = false
                                        },
                                    )
                                },
                                onFailed = {
                                    showPleaseWait = false
                                    statusMessage = context.getString(R.string.ad_not_ready)
                                },
                            )
                        } else {
                            if (canFreeSpin) preferences.markWheelSpun()
                            performSpin(
                                scope, rotation, preferences,
                                onSpinning = { spinning = it },
                                onPrize = { index, message ->
                                    prizeIndex = index
                                    statusMessage = message
                                    spinning = false
                                    canFreeSpin = false
                                },
                            )
                        }
                    }
                },
                enabled = !spinning && (canFreeSpin || freeSpins > 0 || true),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(bottom = 8.dp),
            )
        }

        PleaseWaitOverlay(visible = showPleaseWait)
    }
}

private fun performSpin(
    scope: kotlinx.coroutines.CoroutineScope,
    rotation: androidx.compose.animation.core.Animatable<Float, *>,
    preferences: GamePreferences,
    onSpinning: (Boolean) -> Unit,
    onPrize: (Int, String) -> Unit,
) {
    onSpinning(true)
    val index = Random.nextInt(wheelSegments.size)
    scope.launch {
        spinWheelToIndex(
            animatable = rotation,
            targetIndex = index,
            segmentCount = wheelSegments.size,
            durationMillis = 3000,
        )
        val segment = wheelSegments[index]
        when {
            segment.coins > 0 -> {
                preferences.addCoins(segment.coins)
                val itemId = Analytics.itemLuckyWheel(segment.coins)
                Analytics.reportPurchaseSuccess(itemId, 0.0, Analytics.TYPE_COIN)
                onPrize(index, "+${segment.coins} coins!")
            }
            segment.freeSpins > 0 -> {
                preferences.addFreeSpins(segment.freeSpins)
                onPrize(index, "+${segment.freeSpins} free spins!")
            }
            else -> onPrize(index, "FAIL — try again!")
        }
    }
}

private fun formatCooldown(ms: Long): String {
    val hours = ms / 3_600_000
    val minutes = (ms % 3_600_000) / 60_000
    return "${hours}h ${minutes}m"
}
