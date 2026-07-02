package com.soccerball.goalpop.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.soccerball.goalpop.R
import com.soccerball.goalpop.analytics.Analytics
import com.soccerball.goalpop.data.GamePreferences
import com.soccerball.goalpop.ui.theme.FieldDarkGreen
import com.soccerball.goalpop.ui.theme.FieldGreen
import com.soccerball.goalpop.ui.theme.GoalWhite
import com.soccerball.goalpop.ui.theme.IconNeonGreen
import com.soccerball.goalpop.ui.theme.SkyBlue
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    preferences: GamePreferences,
    onBack: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTermsOfUse: () -> Unit,
) {
    val soundEnabled by preferences.soundEnabled.collectAsState(initial = true)
    val musicEnabled by preferences.musicEnabled.collectAsState(initial = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        Analytics.reportSettingsOpen()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Brush.verticalGradient(listOf(SkyBlue, FieldGreen, FieldDarkGreen))),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(24.dp),
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.displayLarge,
                color = GoalWhite,
            )
            Spacer(modifier = Modifier.height(32.dp))

            SettingRow(
                label = stringResource(R.string.sound_effects),
                checked = soundEnabled,
                onCheckedChange = { enabled ->
                    scope.launch {
                        preferences.setSoundEnabled(enabled)
                    }
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingRow(
                label = stringResource(R.string.music),
                checked = musicEnabled,
                onCheckedChange = { enabled ->
                    scope.launch {
                        preferences.setMusicEnabled(enabled)
                    }
                },
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedButton(onClick = onBack) {
                Text(text = stringResource(R.string.back), color = GoalWhite)
            }
        }

        LegalLinksFooter(
            onPrivacyPolicy = onPrivacyPolicy,
            onTermsOfUse = onTermsOfUse,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun LegalLinksFooter(
    onPrivacyPolicy: () -> Unit,
    onTermsOfUse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(color = androidx.compose.ui.graphics.Color(0xF0121212))
            .border(width = 1.5.dp, color = IconNeonGreen, shape = shape)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LegalLink(
            text = stringResource(R.string.privacy_policy),
            onClick = onPrivacyPolicy,
            modifier = Modifier.weight(1f),
        )
        LegalLink(
            text = stringResource(R.string.terms_of_use),
            onClick = onTermsOfUse,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun LegalLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    Text(
        text = text,
        color = GoalWhite,
        style = MaterialTheme.typography.titleMedium.copy(
            textDecoration = TextDecoration.Underline,
        ),
        textAlign = textAlign,
        modifier = modifier.clickable(onClick = onClick),
    )
}

@Composable
private fun SettingRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            color = GoalWhite,
            modifier = Modifier.weight(1f),
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
