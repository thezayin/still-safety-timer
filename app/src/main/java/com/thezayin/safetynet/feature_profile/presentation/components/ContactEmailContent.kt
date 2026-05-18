package com.thezayin.safetynet.feature_profile.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.ui.theme.SafetyNetTheme
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileState

@Composable
fun ContactEmailContent(
    state: ProfileState,
    onEmailChange: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    bannerAdContent: @Composable () -> Unit = {}
) {
    ProfileFieldContent(
        title = stringResource(R.string.profile_email_question),
        subtitle = stringResource(R.string.profile_email_subtitle),
        value = state.contactEmail,
        placeholder = stringResource(R.string.profile_email_placeholder),
        hintText = stringResource(R.string.profile_email_hint),
        isError = state.contactEmailError != null,
        isLoading = state.isSaving,
        isButtonEnabled = state.isEmailValid,
        keyboardType = KeyboardType.Email,
        onValueChange = onEmailChange,
        onBackClick = onBack,
        onContinueClick = onContinue,
        bannerAdContent = bannerAdContent
    )
}

@Preview(name = "1. Email Setup - Empty", showBackground = true, backgroundColor = 0xFF151921)
@Composable
private fun PreviewContactEmailEmpty() {
    SafetyNetTheme {
        ContactEmailContent(
            state = ProfileState(
                contactEmail = "",
                contactEmailError = AppError.Profile.ContactEmailMissing
            ),
            onEmailChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}

@Preview(name = "2. Email Setup - Invalid Format", showBackground = true, backgroundColor = 0xFF151921)
@Composable
private fun PreviewContactEmailError() {
    SafetyNetTheme {
        ContactEmailContent(
            state = ProfileState(
                contactEmail = "user@com",
                contactEmailError = AppError.Profile.ValidationFailed("email", "Invalid format")
            ),
            onEmailChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}