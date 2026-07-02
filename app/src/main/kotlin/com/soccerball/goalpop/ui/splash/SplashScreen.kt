package com.soccerball.goalpop.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.soccerball.goalpop.R
import com.soccerball.goalpop.ui.theme.FieldDarkGreen
import com.soccerball.goalpop.ui.theme.FieldGreen
import com.soccerball.goalpop.ui.theme.GoalWhite
import com.soccerball.goalpop.ui.theme.SkyBlue
import com.soccerball.goalpop.ui.theme.UiGold
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SkyBlue, FieldGreen, FieldDarkGreen))),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayLarge,
                color = UiGold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "⚽",
                style = MaterialTheme.typography.displayLarge,
            )
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(color = GoalWhite)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.loading),
                color = GoalWhite,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
