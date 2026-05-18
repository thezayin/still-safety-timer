package com.thezayin.safetynet.feature_profile.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.ui.theme.SafetyNetTheme
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileState

@Composable
fun ContactNameContent(
    state: ProfileState,
    onNameChange: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    bannerAdContent: @Composable () -> Unit = {}
) {
    ProfileFieldContent(
        title = stringResource(R.string.profile_contact_question),
        subtitle = stringResource(R.string.profile_contact_subtitle),
        value = state.contactName,
        placeholder = stringResource(R.string.profile_contact_placeholder),
        hintText = stringResource(R.string.profile_contact_hint),
        isError = state.contactNameError != null,
        isLoading = state.isSaving,
        isButtonEnabled = state.isContactNameValid,
        onValueChange = onNameChange,
        onBackClick = onBack,
        onContinueClick = onContinue,
        bannerAdContent = bannerAdContent
    )
}

@Preview(name = "1. Contact Setup - Empty", showBackground = true, backgroundColor = 0xFF151921)
@Composable
private fun PreviewContactNameEmpty() {
    SafetyNetTheme {
        ContactNameContent(
            state = ProfileState(
                contactName = "",
                contactNameError = AppError.Profile.ContactNameMissing
            ),
            onNameChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}

@Preview(
    name = "2. Contact Setup - Valid Input",
    showBackground = true,
    backgroundColor = 0xFF151921
)
@Composable
private fun PreviewContactNameValid() {
    SafetyNetTheme {
        ContactNameContent(
            state = ProfileState(
                contactName = "John Doe",
                contactNameError = null
            ),
            onNameChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}

@Preview(
    name = "3. Contact Setup - Error State",
    showBackground = true,
    backgroundColor = 0xFF151921
)
@Composable
private fun PreviewContactNameError() {
    SafetyNetTheme {
        ContactNameContent(
            state = ProfileState(
                contactName = "J",
                contactNameError = AppError.Profile.ValidationFailed(
                    "contactName",
                    "Must be 2+ characters"
                )
            ),
            onNameChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}