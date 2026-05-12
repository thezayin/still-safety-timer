package com.thezayin.safetynet.feature_profile.presentation

import androidx.lifecycle.viewModelScope
import com.thezayin.safetynet.core.presentation.BaseViewModel
import com.thezayin.safetynet.feature_onboarding.domain.repository.OnboardingRepository
import com.thezayin.safetynet.feature_profile.domain.repository.ProfileRepository
import com.thezayin.safetynet.feature_profile.domain.usecase.SaveProfileDataUseCase
import com.thezayin.safetynet.feature_profile.domain.usecase.ValidateProfileInputUseCase
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileEffect
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileIntent
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileState
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository,
    private val onboardingRepository: OnboardingRepository,
    private val validateUseCase: ValidateProfileInputUseCase,
    private val saveUseCase: SaveProfileDataUseCase
) : BaseViewModel<ProfileState, ProfileIntent, ProfileEffect>(
    initialState = ProfileState()
) {

    init {
        observeProfileData()
    }

    override fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.OnUserNameChanged -> handleUserNameChanged(intent.name)
            is ProfileIntent.OnContactNameChanged -> handleContactNameChanged(intent.name)
            is ProfileIntent.OnContactEmailChanged -> handleContactEmailChanged(intent.email)
            is ProfileIntent.OnSaveUserNameClicked -> saveUserName()
            is ProfileIntent.OnSaveContactNameClicked -> saveContactName()
            is ProfileIntent.OnConfirmProfileClicked -> finalizeProfile()
        }
    }

    private fun observeProfileData() {
        viewModelScope.launch {
            repository.profileData.collect { data ->
                updateState {
                    copy(
                        userName = data.userName,
                        contactName = data.contactName,
                        contactEmail = data.contactEmail,
                        userNameError = validateUseCase.validateUserName(data.userName),
                        contactNameError = validateUseCase.validateContactName(data.contactName),
                        contactEmailError = validateUseCase.validateEmail(data.contactEmail)
                    )
                }
            }
        }
    }

    private fun handleUserNameChanged(name: String) {
        updateState {
            copy(
                userName = name,
                userNameError = validateUseCase.validateUserName(name)
            )
        }
    }

    private fun saveUserName() {
        if (!state.value.isUserNameValid) return
        viewModelScope.launch {
            updateState { copy(isSaving = true) }
            saveUseCase.saveUserName(state.value.userName).fold(
                onSuccess = { sendEffect(ProfileEffect.NavigateToContactName) },
                onFailure = { sendEffect(ProfileEffect.ShowError(it)) }
            )
            updateState { copy(isSaving = false) }
        }
    }

    private fun handleContactNameChanged(name: String) {
        updateState {
            copy(
                contactName = name,
                contactNameError = validateUseCase.validateContactName(name)
            )
        }
    }

    private fun saveContactName() {
        if (!state.value.isContactNameValid) return
        viewModelScope.launch {
            updateState { copy(isSaving = true) }
            saveUseCase.saveContactName(state.value.contactName).fold(
                onSuccess = { sendEffect(ProfileEffect.NavigateToContactEmail) },
                onFailure = { sendEffect(ProfileEffect.ShowError(it)) }
            )
            updateState { copy(isSaving = false) }
        }
    }

    private fun handleContactEmailChanged(email: String) {
        updateState {
            copy(
                contactEmail = email,
                contactEmailError = validateUseCase.validateEmail(email)
            )
        }
    }

    private fun finalizeProfile() {
        if (!state.value.isEmailValid) return
        viewModelScope.launch {
            updateState { copy(isSaving = true) }
            saveUseCase.saveContactEmail(state.value.contactEmail).fold(
                onSuccess = {
                    onboardingRepository.setProfileComplete().fold(
                        onSuccess = { sendEffect(ProfileEffect.NavigateToDashboard) },
                        onFailure = { sendEffect(ProfileEffect.ShowError(it)) }
                    )
                },
                onFailure = { sendEffect(ProfileEffect.ShowError(it)) }
            )
            updateState { copy(isSaving = false) }
        }
    }
}