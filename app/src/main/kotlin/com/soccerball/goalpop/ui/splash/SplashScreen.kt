package com.soccerball.goalpop.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soccerball.goalpop.R
import com.soccerball.goalpop.ui.components.LegalFooter
import com.soccerball.goalpop.ui.components.ReferenceGreenButton
import com.soccerball.goalpop.ui.components.ReferenceLoadingBar
import com.soccerball.goalpop.ui.components.StadiumBackground
import com.soccerball.goalpop.ui.theme.GoalWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    onPrivacyPolicy: () -> Unit = {},
    onTermsOfUse: () -> Unit = {},
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var loadingDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        for (i in 1..20) {
            delay(80)
            progress = i / 20f
        }
        loadingDone = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StadiumBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(0.15f))
            Image(
                painter = painterResource(R.drawable.ui_logo),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.92f),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (!loadingDone) {
                Text(
                    text = stringResource(R.string.loading_your_luck),
                    color = GoalWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp,
                )
                Spacer(modifier = Modifier.height(16.dp))
                ReferenceLoadingBar(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth(0.85f),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (loadingDone) {
                ReferenceGreenButton(
                    text = stringResource(R.string.start_game),
                    onClick = onFinished,
                    modifier = Modifier.fillMaxWidth(0.9f),
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            LegalFooter(
                onPrivacyPolicy = onPrivacyPolicy,
                onTermsOfUse = onTermsOfUse,
                modifier = Modifier.padding(bottom = 16.dp),
            )
        }
    }
}
