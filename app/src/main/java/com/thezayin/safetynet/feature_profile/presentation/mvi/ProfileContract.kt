package com.thezayin.safetynet.feature_profile.presentation.mvi

import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.presentation.UiEffect
import com.thezayin.safetynet.core.presentation.UiIntent
import com.thezayin.safetynet.core.presentation.UiState

data class ProfileState(
    val userName: String = "",
    val userNameError: AppError.Profile? = null,
    val contactName: String = "",
    val contactNameError: AppError.Profile? = null,
    val contactEmail: String = "",
    val contactEmailError: AppError.Profile? = null,
    val isSaving: Boolean = false
) : UiState {
    val isUserNameValid: Boolean = userName.isNotBlank() && userNameError == null
    val isContactNameValid: Boolean = contactName.isNotBlank() && contactNameError == null
    val isEmailValid: Boolean = contactEmail.isNotBlank() && contactEmailError == null
}

sealed interface ProfileIntent : UiIntent {
    data class OnUserNameChanged(val name: String) : ProfileIntent
    data class OnContactNameChanged(val name: String) : ProfileIntent
    data class OnContactEmailChanged(val email: String) : ProfileIntent
    data object OnSaveUserNameClicked : ProfileIntent
    data object OnSaveContactNameClicked : ProfileIntent
    data object OnConfirmProfileClicked : ProfileIntent
}

sealed interface ProfileEffect : UiEffect {
    data object NavigateToContactName : ProfileEffect
    data object NavigateToContactEmail : ProfileEffect
    data object NavigateToDashboard : ProfileEffect
    data class ShowError(val error: AppError) : ProfileEffect
}