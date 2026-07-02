package com.soccerball.goalpop.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.soccerball.goalpop.R
import com.soccerball.goalpop.data.GamePreferences
import com.soccerball.goalpop.ui.components.LegalFooter
import com.soccerball.goalpop.ui.components.ReferenceGreenButton
import com.soccerball.goalpop.ui.components.ReferenceHudIconSettings
import com.soccerball.goalpop.ui.components.ReferenceHudIconTrophy
import com.soccerball.goalpop.ui.components.ReferenceHudIconWheel
import com.soccerball.goalpop.ui.components.StadiumBackground
import com.soccerball.goalpop.ui.dialogs.DailyBonusDialog
import kotlinx.coroutines.launch

@Composable
fun MainMenuScreen(
    preferences: GamePreferences,
    onPlay: (level: Int) -> Unit,
    onShop: () -> Unit,
    onLuckyWheel: () -> Unit,
    onSettings: () -> Unit,
    onLeaderboard: () -> Unit,
    onPrivacyPolicy: () -> Unit = {},
    onTermsOfUse: () -> Unit = {},
) {
    val currentLevel by preferences.currentLevel.collectAsState(initial = 1)
    val scope = rememberCoroutineScope()
    var showDailyBonus by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (preferences.canClaimDailyBonus()) {
            showDailyBonus = true
        }
    }

    if (showDailyBonus) {
        DailyBonusDialog(
            rewardAmount = GamePreferences.DAILY_BONUS_COINS,
            onClaim = {
                scope.launch {
                    preferences.claimDailyBonus()
                    showDailyBonus = false
                }
            },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StadiumBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.weight(1f))
                ReferenceHudIconSettings(onClick = onSettings)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                ReferenceHudIconWheel(onClick = onLuckyWheel)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                ReferenceHudIconTrophy(onClick = onLeaderboard)
            }

            Spacer(modifier = Modifier.weight(0.12f))
            Image(
                painter = painterResource(R.drawable.ui_logo),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.95f),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.weight(1f))

            ReferenceGreenButton(
                text = stringResource(R.string.start_game),
                onClick = { onPlay(currentLevel) },
                modifier = Modifier.fillMaxWidth(0.9f),
            )
            Spacer(modifier = Modifier.height(20.dp))
            LegalFooter(
                onPrivacyPolicy = onPrivacyPolicy,
                onTermsOfUse = onTermsOfUse,
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }
    }
}
