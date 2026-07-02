package com.soccerball.goalpop.ui.shop

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.soccerball.goalpop.R
import com.soccerball.goalpop.ads.RewardedAdManager
import com.soccerball.goalpop.analytics.Analytics
import com.soccerball.goalpop.data.GamePreferences
import com.soccerball.goalpop.ui.components.pulseScale
import com.soccerball.goalpop.ui.theme.FieldDarkGreen
import com.soccerball.goalpop.ui.theme.FieldGreen
import com.soccerball.goalpop.ui.theme.GoalWhite
import com.soccerball.goalpop.ui.theme.SkyBlue
import com.soccerball.goalpop.ui.theme.UiDark
import com.soccerball.goalpop.ui.theme.UiGold
import kotlinx.coroutines.launch

@Composable
fun ShopScreen(
    preferences: GamePreferences,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val rewardedAds = remember { RewardedAdManager.getInstance(app) }
    val coins by preferences.coins.collectAsState(initial = 0)
    val scope = rememberCoroutineScope()
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var purchasedThisVisit by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Analytics.reportPaywallView(Analytics.SOURCE_SHOP)
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
            text = stringResource(R.string.shop),
            style = MaterialTheme.typography.displayLarge,
            color = UiGold,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${stringResource(R.string.coins)}: $coins",
            style = MaterialTheme.typography.headlineMedium,
            color = GoalWhite,
        )
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(R.string.shop_free_coins_title),
            style = MaterialTheme.typography.titleLarge,
            color = GoalWhite,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.shop_free_coins_desc),
            style = MaterialTheme.typography.bodyLarge,
            color = GoalWhite.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                statusMessage = null
                Analytics.reportPurchaseClick(Analytics.ITEM_FREE_COINS_1000, Analytics.TYPE_COIN)
                rewardedAds.showRewarded(
                    onReward = {
                        scope.launch {
                            preferences.addCoins(GamePreferences.SHOP_VIDEO_REWARD)
                        }
                        Analytics.reportPurchaseSuccess(
                            Analytics.ITEM_FREE_COINS_1000,
                            0.0,
                            Analytics.TYPE_COIN,
                        )
                        purchasedThisVisit = true
                        statusMessage = context.getString(R.string.coins_added, GamePreferences.SHOP_VIDEO_REWARD)
                    },
                    onFailed = {
                        Analytics.reportPurchaseError(Analytics.ITEM_FREE_COINS_1000, Analytics.TYPE_COIN)
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_video_reward),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = stringResource(R.string.watch_video_coins, GamePreferences.SHOP_VIDEO_REWARD),
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }

        statusMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = UiGold,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = {
                if (!purchasedThisVisit) {
                    Analytics.reportPaywallClose(Analytics.SOURCE_SHOP)
                }
                onBack()
            },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .navigationBarsPadding(),
        ) {
            Text(
                text = stringResource(R.string.back),
                color = GoalWhite,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }
}
