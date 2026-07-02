package com.soccerball.goalpop.ui.settings

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.soccerball.goalpop.R
import com.soccerball.goalpop.analytics.Analytics
import com.soccerball.goalpop.data.GamePreferences
import com.soccerball.goalpop.ui.components.ReferenceBackButton
import com.soccerball.goalpop.ui.components.ReferenceModalPanel
import com.soccerball.goalpop.ui.components.ReferenceScreenTitle
import com.soccerball.goalpop.ui.components.ReferenceToggleRow
import com.soccerball.goalpop.ui.components.ReferenceVolumeSlider
import com.soccerball.goalpop.ui.components.StadiumBackground
import com.soccerball.goalpop.ui.theme.GoalWhite
import com.soccerball.goalpop.ui.theme.IconNeonGreen
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    preferences: GamePreferences,
    onBack: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTermsOfUse: () -> Unit,
    onStore: () -> Unit = {},
) {
    val soundVolume by preferences.soundVolume.collectAsState(initial = 100)
    val musicVolume by preferences.musicVolume.collectAsState(initial = 100)
    val vibrationEnabled by preferences.vibrationEnabled.collectAsState(initial = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        Analytics.reportSettingsOpen()
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
                ReferenceScreenTitle(text = stringResource(R.string.settings))
            }

            Spacer(modifier = Modifier.height(24.dp))

            ReferenceModalPanel(modifier = Modifier.fillMaxWidth()) {
                ReferenceVolumeSlider(
                    label = stringResource(R.string.sound),
                    value = soundVolume / 100f,
                    onValueChange = { value ->
                        scope.launch {
                            preferences.setSoundVolume((value * 100).toInt())
                        }
                    },
                )
                Spacer(modifier = Modifier.height(20.dp))
                ReferenceVolumeSlider(
                    label = stringResource(R.string.music),
                    value = musicVolume / 100f,
                    onValueChange = { value ->
                        scope.launch {
                            preferences.setMusicVolume((value * 100).toInt())
                        }
                    },
                )
                Spacer(modifier = Modifier.height(20.dp))
                ReferenceToggleRow(
                    label = stringResource(R.string.vibration),
                    checked = vibrationEnabled,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            preferences.setVibrationEnabled(enabled)
                        }
                    },
                )
                Spacer(modifier = Modifier.height(28.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.privacy_policy),
                        color = GoalWhite,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onPrivacyPolicy),
                    )
                    Text(
                        text = stringResource(R.string.store),
                        color = IconNeonGreen,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onStore),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.terms_of_use),
                    color = GoalWhite,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onTermsOfUse),
                )
            }
        }
    }
}
