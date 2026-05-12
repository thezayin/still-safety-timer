package com.thezayin.safetynet.feature_profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thezayin.safetynet.feature_profile.presentation.components.ContactEmailContent
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileEffect
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileIntent

@Composable
fun ContactEmailScreen(
    viewModel: ProfileViewModel, onNavigateNext: () -> Unit, onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            if (effect is ProfileEffect.NavigateToDashboard) {
                onNavigateNext()
            }
        }
    }

    ContactEmailContent(
        state = state,
        onEmailChange = { viewModel.onIntent(ProfileIntent.OnContactEmailChanged(it)) },
        onBack = onNavigateBack,
        onContinue = { viewModel.onIntent(ProfileIntent.OnConfirmProfileClicked) })
}