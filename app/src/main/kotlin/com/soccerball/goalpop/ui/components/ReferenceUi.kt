package com.soccerball.goalpop.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soccerball.goalpop.R
import com.soccerball.goalpop.ui.theme.GoalWhite
import com.soccerball.goalpop.ui.theme.IconNeonGreen
import java.text.NumberFormat
import java.util.Locale

val RefPanelBg = Color(0xF0121212)
val RefGreenLight = Color(0xFF7CFC00)
val RefGreenDark = Color(0xFF2E7D32)
val RefRed = Color(0xFFE53935)
val RefGold = Color(0xFFFFD700)

fun formatHudScore(score: Int): String {
    val formatted = NumberFormat.getNumberInstance(Locale.US).format(score)
    return "SCORE:$formatted"
}

@Composable
fun StadiumBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.ui_stadium_strip),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f)),
        )
    }
}

@Composable
fun ReferenceModalPanel(
    modifier: Modifier = Modifier,
    borderColor: Color = IconNeonGreen,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(RefPanelBg)
            .border(2.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}

@Composable
fun ReferenceGreenButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(32.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            .background(
                if (enabled) {
                    Brush.verticalGradient(listOf(RefGreenLight, RefGreenDark))
                } else {
                    Brush.verticalGradient(listOf(Color.Gray, Color.DarkGray))
                },
                shape,
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = GoalWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
fun ReferenceDarkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(32.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(listOf(Color(0xFF3A3A3A), Color(0xFF1A1A1A))),
                shape,
            )
            .border(1.dp, Color(0xFF666666), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = GoalWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
fun ReferenceRedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(32.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(listOf(Color(0xFFFF5252), Color(0xFFB71C1C))),
                shape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = GoalWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
fun ReferenceLoadingBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(300),
        label = "loading",
    )
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, IconNeonGreen.copy(alpha = 0.5f), shape),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animated)
                .height(14.dp)
                .background(
                    Brush.horizontalGradient(listOf(RefGreenDark, RefGreenLight)),
                    shape,
                ),
        )
    }
}

@Composable
fun LegalFooter(
    onPrivacyPolicy: () -> Unit,
    onTermsOfUse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "By tapping \"Let's Play\" you confirm that you 18+ and",
            color = GoalWhite.copy(alpha = 0.85f),
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
        )
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                text = "our ",
                color = GoalWhite.copy(alpha = 0.85f),
                fontSize = 11.sp,
            )
            Text(
                text = "Terms Of Use",
                color = GoalWhite,
                fontSize = 11.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onTermsOfUse),
            )
            Text(
                text = " & ",
                color = GoalWhite.copy(alpha = 0.85f),
                fontSize = 11.sp,
            )
            Text(
                text = "Privacy Policy",
                color = GoalWhite,
                fontSize = 11.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onPrivacyPolicy),
            )
        }
    }
}

@Composable
fun ReferenceHudScoreBox(
    score: Int,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color(0xE6000000))
            .border(1.5.dp, IconNeonGreen, shape)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(
            text = formatHudScore(score),
            color = IconNeonGreen,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
fun ReferenceHudLevelBox(
    level: Int,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color(0xE6000000))
            .border(1.5.dp, IconNeonGreen, shape)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text = "LVL $level",
            color = IconNeonGreen,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun ReferenceHudIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(shape)
            .background(Color(0xE6000000))
            .border(1.5.dp, IconNeonGreen, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        icon()
    }
}

@Composable
fun ReferenceHudIconSettings(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ReferenceHudIconButton(onClick = onClick, modifier = modifier) {
        Icon(Icons.Default.Settings, contentDescription = null, tint = IconNeonGreen, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun ReferenceHudIconWheel(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ReferenceHudIconButton(onClick = onClick, modifier = modifier) {
        Icon(Icons.Default.Star, contentDescription = null, tint = RefGold, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun ReferenceHudIconTrophy(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ReferenceHudIconButton(onClick = onClick, modifier = modifier) {
        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = RefGold, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun ReferenceBottomHudBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(shape)
            .background(Color(0xE6000000))
            .border(1.5.dp, IconNeonGreen, shape),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
fun ReferenceBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(shape)
            .background(Color(0xE6000000))
            .border(2.dp, RefGold, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            tint = RefGold,
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
fun ReferenceVolumeSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = GoalWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = IconNeonGreen,
                activeTrackColor = IconNeonGreen,
                inactiveTrackColor = Color(0xFF333333),
            ),
        )
    }
}

@Composable
fun ReferenceToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = GoalWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GoalWhite,
                checkedTrackColor = IconNeonGreen,
                uncheckedThumbColor = GoalWhite,
                uncheckedTrackColor = Color(0xFF444444),
            ),
        )
    }
}

@Composable
fun ReferenceWheelImage(
    rotationDegrees: Float,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(R.drawable.ui_wheel_base),
        contentDescription = null,
        modifier = modifier.graphicsLayer {
            rotationZ = rotationDegrees
        },
        contentScale = ContentScale.Fit,
    )
}

@Composable
fun ReferenceScreenTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = GoalWhite,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 2.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
fun ReferenceSectionHeader(
    text: String,
    color: Color = IconNeonGreen,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}
