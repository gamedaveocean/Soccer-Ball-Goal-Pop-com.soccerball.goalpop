package com.soccerball.goalpop.ui.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.soccerball.goalpop.R
import com.soccerball.goalpop.ui.components.ReferenceDarkButton
import com.soccerball.goalpop.ui.components.ReferenceGreenButton
import com.soccerball.goalpop.ui.components.ReferenceModalPanel
import com.soccerball.goalpop.ui.components.ReferenceSectionHeader

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
        ReferenceModalPanel(modifier = Modifier.fillMaxWidth()) {
            ReferenceSectionHeader(text = stringResource(R.string.pause))
            Spacer(modifier = Modifier.height(24.dp))
            ReferenceGreenButton(
                text = stringResource(R.string.resume),
                onClick = onResume,
            )
            Spacer(modifier = Modifier.height(12.dp))
            ReferenceDarkButton(
                text = stringResource(R.string.home),
                onClick = onMainMenu,
            )
        }
    }
}
