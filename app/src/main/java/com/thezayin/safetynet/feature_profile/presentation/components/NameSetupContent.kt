package com.thezayin.safetynet.feature_profile.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.ui.theme.SafetyNetTheme
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileState

@Composable
fun NameSetupContent(
    state: ProfileState,
    onNameChange: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    ProfileFieldContent(
        title = stringResource(R.string.profile_name_question),
        subtitle = stringResource(R.string.profile_name_subtitle),
        value = state.userName,
        placeholder = stringResource(R.string.profile_name_placeholder),
        hintText = stringResource(R.string.profile_name_hint),
        isError = state.userNameError != null,
        isLoading = state.isSaving,
        isButtonEnabled = state.isUserNameValid,
        onValueChange = onNameChange,
        onBackClick = onBack,
        onContinueClick = onContinue
    )
}

@Preview(name = "1. Name Setup - Empty", showBackground = true, backgroundColor = 0xFF151921)
@Composable
private fun PreviewNameSetupEmpty() {
    SafetyNetTheme {
        NameSetupContent(
            state = ProfileState(
                userName = "",
                userNameError = AppError.Profile.UserNameMissing
            ),
            onNameChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}

@Preview(name = "2. Name Setup - Valid Input", showBackground = true, backgroundColor = 0xFF151921)
@Composable
private fun PreviewNameSetupValid() {
    SafetyNetTheme {
        NameSetupContent(
            state = ProfileState(
                userName = "Zayin Software",
                userNameError = null
            ),
            onNameChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}

@Preview(name = "3. Name Setup - Error State", showBackground = true, backgroundColor = 0xFF151921)
@Composable
private fun PreviewNameSetupError() {
    SafetyNetTheme {
        NameSetupContent(
            state = ProfileState(
                userName = "Z",
                userNameError = AppError.Profile.ValidationFailed(
                    "userName",
                    "Must be 2+ characters"
                )
            ),
            onNameChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}

@Preview(name = "4. Name Setup - Saving State", showBackground = true, backgroundColor = 0xFF151921)
@Composable
private fun PreviewNameSetupSaving() {
    SafetyNetTheme {
        NameSetupContent(
            state = ProfileState(
                userName = "Zayin Software",
                isSaving = true
            ),
            onNameChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}