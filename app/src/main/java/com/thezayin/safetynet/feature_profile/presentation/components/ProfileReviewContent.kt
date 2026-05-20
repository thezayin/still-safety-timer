package com.thezayin.safetynet.feature_profile.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ui.components.PrimaryButton
import com.thezayin.safetynet.core.ui.theme.SafetyNetTheme
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileState

@Composable
fun ProfileReviewContent(
    state: ProfileState,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    bannerAdContent: @Composable () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ProfileBackground()

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.btn_back),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 32.dp)
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = stringResource(R.string.profile_review_question),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 48.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.profile_review_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            ReviewItem(
                label = stringResource(R.string.profile_name_placeholder),
                value = state.userName.ifBlank { "—" }
            )
            ReviewItem(
                label = stringResource(R.string.profile_contact_placeholder),
                value = state.contactName.ifBlank { "—" }
            )
            ReviewItem(
                label = stringResource(R.string.profile_email_placeholder),
                value = state.contactEmail.ifBlank { "—" }
            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = stringResource(R.string.profile_btn_confirm),
                onClick = onConfirm,
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            )

            bannerAdContent()
        }
    }
}

@Preview(name = "Review - Full Data", showBackground = true, backgroundColor = 0xFF151921)
@Composable
private fun PreviewReviewFull() {
    SafetyNetTheme {
        ProfileReviewContent(
            state = ProfileState(
                userName = "John M.",
                contactName = "Emergency Contact",
                contactEmail = "alert@safety.net"
            ),
            onBack = {},
            onConfirm = {},
            bannerAdContent = {}
        )
    }
}