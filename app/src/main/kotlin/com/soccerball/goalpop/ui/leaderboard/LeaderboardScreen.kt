package com.soccerball.goalpop.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soccerball.goalpop.R
import com.soccerball.goalpop.data.GamePreferences
import com.soccerball.goalpop.ui.components.ReferenceBackButton
import com.soccerball.goalpop.ui.components.ReferenceModalPanel
import com.soccerball.goalpop.ui.components.ReferenceScreenTitle
import com.soccerball.goalpop.ui.components.RefGold
import com.soccerball.goalpop.ui.components.StadiumBackground
import com.soccerball.goalpop.ui.theme.GoalWhite
import com.soccerball.goalpop.ui.theme.IconNeonGreen
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LeaderboardScreen(
    preferences: GamePreferences,
    onBack: () -> Unit,
) {
    val highScore by preferences.highScore.collectAsState(initial = 0)
    val playerName by preferences.playerName.collectAsState(initial = "Player")
    val entries = remember(highScore, playerName) {
        preferences.leaderboardEntries(highScore, playerName)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StadiumBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ReferenceBackButton(onClick = onBack)
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = RefGold,
                    modifier = Modifier.padding(end = 8.dp),
                )
                ReferenceScreenTitle(text = stringResource(R.string.leaderboard))
            }

            Spacer(modifier = Modifier.height(20.dp))

            ReferenceModalPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    entries.forEach { entry ->
                        LeaderboardRow(entry = entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: com.soccerball.goalpop.data.LeaderboardEntry) {
    val shape = RoundedCornerShape(10.dp)
    val trophyColor = when (entry.rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> GoalWhite.copy(alpha = 0.6f)
    }
    val formatted = NumberFormat.getNumberInstance(Locale.US).format(entry.score)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, IconNeonGreen.copy(alpha = 0.4f), shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (entry.rank <= 3) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = trophyColor,
                modifier = Modifier.padding(end = 8.dp),
            )
        } else {
            Text(
                text = "${entry.rank}",
                color = GoalWhite.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.width(32.dp),
            )
        }
        Text(
            text = entry.name,
            color = GoalWhite,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(IconNeonGreen.copy(alpha = 0.2f))
                .border(1.dp, IconNeonGreen, RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(
                text = formatted,
                color = IconNeonGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
    }
}
