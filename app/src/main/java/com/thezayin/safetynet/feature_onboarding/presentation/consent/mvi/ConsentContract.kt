package com.thezayin.safetynet.feature_onboarding.presentation.consent.mvi

import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.presentation.UiEffect
import com.thezayin.safetynet.core.presentation.UiIntent
import com.thezayin.safetynet.core.presentation.UiState

data class ConsentState(
    val isAccepted: Boolean = false,
    val isSubmitting: Boolean = false
) : UiState

sealed interface ConsentIntent : UiIntent {
    data class OnToggleAcceptance(val isChecked: Boolean) : ConsentIntent
    data object OnSubmitClicked : ConsentIntent
    data object OnDeclineClicked : ConsentIntent
}

sealed interface ConsentEffect : UiEffect {
    data object NavigateToPermissions : ConsentEffect
    data object ExitApp : ConsentEffect
    data class ShowError(val error: AppError) : ConsentEffect
}