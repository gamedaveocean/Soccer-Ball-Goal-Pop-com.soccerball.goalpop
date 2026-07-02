package com.soccerball.goalpop.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.soccerball.goalpop.R
import com.soccerball.goalpop.data.GamePreferences
import com.soccerball.goalpop.ui.components.RewardWheel
import com.soccerball.goalpop.ui.components.pulseScale
import com.soccerball.goalpop.ui.theme.FieldDarkGreen
import com.soccerball.goalpop.ui.theme.FieldGreen
import com.soccerball.goalpop.ui.theme.GoalWhite
import com.soccerball.goalpop.ui.theme.SkyBlue
import com.soccerball.goalpop.ui.theme.UiDark
import com.soccerball.goalpop.ui.theme.UiGold

@Composable
fun MainMenuScreen(
    preferences: GamePreferences,
    onPlay: (level: Int) -> Unit,
    onShop: () -> Unit,
    onLuckyWheel: () -> Unit,
    onSettings: () -> Unit,
) {
    val highScore by preferences.highScore.collectAsState(initial = 0)
    val currentLevel by preferences.currentLevel.collectAsState(initial = 1)
    val coins by preferences.coins.collectAsState(initial = 0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SkyBlue, FieldGreen, FieldDarkGreen))),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            val wheelSize = (maxWidth * 0.34f).coerceIn(120.dp, 160.dp)
            val actionButtonWidth = if (maxWidth < 360.dp) 0.82f else 0.7f

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayLarge,
                    color = UiGold,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${stringResource(R.string.coins)}: $coins",
                    style = MaterialTheme.typography.titleLarge,
                    color = GoalWhite,
                )
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(wheelSize)
                        .pulseScale()
                        .clickable(onClick = onLuckyWheel),
                    contentAlignment = Alignment.Center,
                ) {
                    RewardWheel(
                        segments = listOf("100", "250", "500", "1K", "2K", "?"),
                        segmentColors = listOf(
                            Color(0xFFE53935),
                            Color(0xFFFB8C00),
                            Color(0xFFFFC107),
                            Color(0xFF43A047),
                            Color(0xFF1E88E5),
                            Color(0xFF8E24AA),
                        ),
                        rotationDegrees = 0f,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                Text(
                    text = stringResource(R.string.tap),
                    style = MaterialTheme.typography.titleMedium,
                    color = UiGold,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.lucky_wheel),
                    style = MaterialTheme.typography.bodyMedium,
                    color = GoalWhite.copy(alpha = 0.85f),
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onPlay(currentLevel) },
                    modifier = Modifier.fillMaxWidth(actionButtonWidth),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UiGold,
                        contentColor = UiDark,
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.play),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onShop,
                    modifier = Modifier.fillMaxWidth(actionButtonWidth),
                ) {
                    Text(
                        text = stringResource(R.string.shop),
                        color = GoalWhite,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onSettings,
                    modifier = Modifier.fillMaxWidth(actionButtonWidth),
                ) {
                    Text(
                        text = stringResource(R.string.settings),
                        color = GoalWhite,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "${stringResource(R.string.high_score)}: $highScore",
                    style = MaterialTheme.typography.titleLarge,
                    color = GoalWhite,
                )
                Text(
                    text = "${stringResource(R.string.level)}: $currentLevel",
                    style = MaterialTheme.typography.bodyLarge,
                    color = GoalWhite.copy(alpha = 0.8f),
                )
            }
        }
    }
}

