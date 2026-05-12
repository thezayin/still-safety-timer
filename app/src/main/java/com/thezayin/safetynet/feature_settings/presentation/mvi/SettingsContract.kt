package com.thezayin.safetynet.feature_settings.presentation.mvi

import com.thezayin.safetynet.core.presentation.UiEffect
import com.thezayin.safetynet.core.presentation.UiIntent
import com.thezayin.safetynet.core.presentation.UiState

data class SettingsState(
    val userName: String = "",
    val contactName: String = "",
    val contactEmail: String = "",
    val intervalHours: Int = 24,
    val isNotificationsReady: Boolean = false,
    val isExactAlarmReady: Boolean = false,
    val isBatteryOptimized: Boolean = false,
    val isLoading: Boolean = false
) : UiState

sealed interface SettingsIntent : UiIntent {
    data class UpdateUserName(val name: String) : SettingsIntent
    data class UpdateContactName(val name: String) : SettingsIntent
    data class UpdateContactEmail(val email: String) : SettingsIntent
    data class UpdateInterval(val hours: Int) : SettingsIntent

    data object ContactSupport : SettingsIntent
    data object OpenPrivacyPolicy : SettingsIntent
    data object OpenTermsOfService : SettingsIntent
    data object WipeAllData : SettingsIntent
    data object RefreshPermissionStatus : SettingsIntent
}

sealed interface SettingsEffect : UiEffect {
    data class ShowError(val message: String) : SettingsEffect
    data object NavigateToSplash : SettingsEffect
    data class OpenUrl(val url: String) : SettingsEffect
    data class OpenEmailClient(val address: String, val subject: String) : SettingsEffect
}