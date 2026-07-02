package com.soccerball.goalpop.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.soccerball.goalpop.R
import com.soccerball.goalpop.ui.components.ReferenceGreenButton
import com.soccerball.goalpop.ui.components.ReferenceLoadingBar
import com.soccerball.goalpop.ui.components.ReferenceModalPanel
import com.soccerball.goalpop.ui.components.ReferenceSectionHeader
import com.soccerball.goalpop.ui.components.StadiumBackground
import com.soccerball.goalpop.ui.theme.GoalWhite
import com.soccerball.goalpop.ui.theme.IconNeonGreen

@Composable
fun DailyBonusDialog(
    rewardAmount: Int,
    onClaim: () -> Unit,
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            ReferenceModalPanel(modifier = Modifier.fillMaxWidth()) {
                ReferenceSectionHeader(text = stringResource(R.string.daily_bonus_title))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.daily_bonus_subtitle),
                    color = GoalWhite.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    painter = painterResource(R.drawable.ui_bonus_chest),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(180.dp),
                    contentScale = ContentScale.Fit,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "🪙 $rewardAmount",
                    color = GoalWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                )
                Spacer(modifier = Modifier.height(24.dp))
                ReferenceGreenButton(
                    text = stringResource(R.string.claim),
                    onClick = onClaim,
                )
            }
        }
    }
}

@Composable
fun GameResultDialog(
    won: Boolean,
    score: Int,
    onPrimary: () -> Unit,
    onHome: () -> Unit,
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {
        ReferenceModalPanel(
            borderColor = if (won) IconNeonGreen else Color(0xFFE53935),
        ) {
            ReferenceSectionHeader(
                text = if (won) {
                    stringResource(R.string.level_complete)
                } else {
                    stringResource(R.string.game_over)
                },
                color = if (won) IconNeonGreen else Color(0xFFFF5252),
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.your_score),
                color = GoalWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "$score",
                color = GoalWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
            )
            Spacer(modifier = Modifier.height(28.dp))
            if (won) {
                ReferenceGreenButton(
                    text = stringResource(R.string.next),
                    onClick = onPrimary,
                )
            } else {
                com.soccerball.goalpop.ui.components.ReferenceRedButton(
                    text = stringResource(R.string.try_again),
                    onClick = onPrimary,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            com.soccerball.goalpop.ui.components.ReferenceDarkButton(
                text = stringResource(R.string.home),
                onClick = onHome,
            )
        }
    }
}

@Composable
fun PleaseWaitOverlay(
    progress: Float = 0.65f,
    visible: Boolean = true,
) {
    if (!visible) return

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        StadiumBackground()
        ReferenceModalPanel(modifier = Modifier.padding(horizontal = 32.dp)) {
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = IconNeonGreen,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(72.dp),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            ReferenceSectionHeader(text = stringResource(R.string.please_wait))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.please_wait_desc),
                color = GoalWhite.copy(alpha = 0.8f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))
            ReferenceLoadingBar(progress = progress, modifier = Modifier.fillMaxWidth())
        }
    }
}
