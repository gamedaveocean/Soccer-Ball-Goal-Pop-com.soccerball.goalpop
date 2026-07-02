package com.soccerball.goalpop.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.soccerball.goalpop.R
import com.soccerball.goalpop.ui.theme.FieldDarkGreen
import com.soccerball.goalpop.ui.theme.GoalWhite
import com.soccerball.goalpop.ui.theme.UiDark
import com.soccerball.goalpop.ui.theme.UiGold

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onMainMenu: () -> Unit,
) {
    Dialog(
        onDismissRequest = onResume,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(FieldDarkGreen, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.pause),
                style = MaterialTheme.typography.headlineMedium,
                color = UiGold,
                textAlign = TextAlign.Center,
            )

            Button(
                onClick = onResume,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = UiGold,
                    contentColor = UiDark,
                ),
            ) {
                Text(
                    text = stringResource(R.string.resume),
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }

            OutlinedButton(
                onClick = onMainMenu,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.main_menu),
                    color = GoalWhite,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}
