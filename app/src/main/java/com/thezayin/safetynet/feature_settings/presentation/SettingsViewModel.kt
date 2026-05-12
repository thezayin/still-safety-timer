package com.thezayin.safetynet.feature_settings.presentation

import androidx.lifecycle.viewModelScope
import com.thezayin.safetynet.core.presentation.BaseViewModel
import com.thezayin.safetynet.feature_onboarding.domain.permission.PermissionChecker
import com.thezayin.safetynet.feature_profile.domain.repository.ProfileRepository
import com.thezayin.safetynet.feature_settings.domain.repository.SettingsRepository
import com.thezayin.safetynet.feature_settings.domain.usecase.GetSupportEmailUseCase
import com.thezayin.safetynet.feature_settings.domain.usecase.UpdateIntervalUseCase
import com.thezayin.safetynet.feature_settings.domain.usecase.WipeAllDataUseCase
import com.thezayin.safetynet.feature_settings.presentation.mvi.SettingsEffect
import com.thezayin.safetynet.feature_settings.presentation.mvi.SettingsIntent
import com.thezayin.safetynet.feature_settings.presentation.mvi.SettingsState
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val profileRepository: ProfileRepository,
    private val timerRepository: TimerRepository,
    private val settingsRepository: SettingsRepository,
    private val permissionChecker: PermissionChecker,
    private val wipeAllDataUseCase: WipeAllDataUseCase,
    private val updateIntervalUseCase: UpdateIntervalUseCase,
    private val getSupportEmailUseCase: GetSupportEmailUseCase
) : BaseViewModel<SettingsState, SettingsIntent, SettingsEffect>(
    initialState = SettingsState()
) {

    init {
        observeDataStreams()
        permissionChecker.checkAllPermissions()
    }

    private fun observeDataStreams() {
        combine(
            profileRepository.profileData,
            timerRepository.timerData,
            permissionChecker.observePermissionStatus()
        ) { profile, timer, permissions ->
            updateState {
                copy(
                    userName = profile.userName,
                    contactName = profile.contactName,
                    contactEmail = profile.contactEmail,
                    intervalHours = timer.durationHours,
                    isNotificationsReady = permissions.notifications,
                    isExactAlarmReady = permissions.exactAlarms
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.UpdateUserName -> handleSaveUserName(intent.name)
            is SettingsIntent.UpdateContactName -> handleSaveContactName(intent.name)
            is SettingsIntent.UpdateContactEmail -> handleSaveContactEmail(intent.email)
            is SettingsIntent.UpdateInterval -> updateInterval(intent.hours)
            SettingsIntent.ContactSupport -> handleContactSupport()
            SettingsIntent.OpenPrivacyPolicy -> openUrl { it.privacyPolicyUrl }
            SettingsIntent.OpenTermsOfService -> openUrl { it.termsUrl }
            SettingsIntent.WipeAllData -> handleWipeData()
            SettingsIntent.RefreshPermissionStatus -> permissionChecker.checkAllPermissions()
        }
    }

    private fun handleSaveUserName(name: String) {
        viewModelScope.launch { profileRepository.saveUserName(name) }
    }

    private fun handleSaveContactName(name: String) {
        viewModelScope.launch { profileRepository.saveContactName(name) }
    }

    private fun handleSaveContactEmail(email: String) {
        viewModelScope.launch { profileRepository.saveContactEmail(email) }
    }

    private fun updateInterval(hours: Int) {
        viewModelScope.launch { updateIntervalUseCase(hours) }
    }

    private fun handleContactSupport() {
        viewModelScope.launch {
            val (email, subject) = getSupportEmailUseCase(state.value.userName)
            sendEffect(SettingsEffect.OpenEmailClient(email, subject))
        }
    }

    private fun openUrl(urlSelector: (SettingsRepository) -> kotlinx.coroutines.flow.Flow<String>) {
        viewModelScope.launch {
            val url = urlSelector(settingsRepository).first()
            sendEffect(SettingsEffect.OpenUrl(url))
        }
    }

    private fun handleWipeData() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            wipeAllDataUseCase().fold(
                onSuccess = { sendEffect(SettingsEffect.NavigateToSplash) },
                onFailure = { sendEffect(SettingsEffect.ShowError("System reset failed.")) })
            updateState { copy(isLoading = false) }
        }
    }
}