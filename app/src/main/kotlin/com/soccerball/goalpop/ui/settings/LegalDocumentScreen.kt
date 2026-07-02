package com.soccerball.goalpop.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.soccerball.goalpop.R
import com.soccerball.goalpop.ui.theme.FieldDarkGreen
import com.soccerball.goalpop.ui.theme.FieldGreen
import com.soccerball.goalpop.ui.theme.GoalWhite
import com.soccerball.goalpop.ui.theme.SkyBlue

enum class LegalDocument {
    PrivacyPolicy,
    TermsOfUse,
}

@Composable
fun LegalDocumentScreen(
    document: LegalDocument,
    onBack: () -> Unit,
) {
    val title = when (document) {
        LegalDocument.PrivacyPolicy -> stringResource(R.string.privacy_policy)
        LegalDocument.TermsOfUse -> stringResource(R.string.terms_of_use)
    }
    val body = when (document) {
        LegalDocument.PrivacyPolicy -> stringResource(R.string.privacy_policy_text)
        LegalDocument.TermsOfUse -> stringResource(R.string.terms_of_use_text)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Brush.verticalGradient(listOf(SkyBlue, FieldGreen, FieldDarkGreen)))
            .padding(24.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            color = GoalWhite,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = GoalWhite.copy(alpha = 0.92f),
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.navigationBarsPadding(),
        ) {
            Text(text = stringResource(R.string.back), color = GoalWhite)
        }
    }
}
