package com.soccerball.goalpop.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme = darkColorScheme(
    primary = UiGold,
    onPrimary = UiDark,
    secondary = FieldLightGreen,
    onSecondary = GoalWhite,
    background = FieldDarkGreen,
    onBackground = GoalWhite,
    surface = FieldGreen,
    onSurface = GoalWhite,
)

@Composable
fun SoccerBallTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content,
    )
}
