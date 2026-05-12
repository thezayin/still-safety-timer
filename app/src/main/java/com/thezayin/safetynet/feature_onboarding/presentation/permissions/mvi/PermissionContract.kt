package com.thezayin.safetynet.feature_onboarding.presentation.permissions.mvi

import com.thezayin.safetynet.core.presentation.UiEffect
import com.thezayin.safetynet.core.presentation.UiIntent
import com.thezayin.safetynet.core.presentation.UiState
import com.thezayin.safetynet.feature_onboarding.domain.permission.SafetyPermissionStatus

data class PermissionState(
    val status: SafetyPermissionStatus = SafetyPermissionStatus()
) : UiState

sealed interface PermissionIntent : UiIntent {
    data object RefreshStatus : PermissionIntent
    data object RequestNotification : PermissionIntent
    data object RequestAlarm : PermissionIntent
    data object OnContinueClicked : PermissionIntent
}

sealed interface PermissionEffect : UiEffect {
    data object TriggerNotificationPopup : PermissionEffect
    data object OpenAlarmSettings : PermissionEffect
    data object NavigateToProfile : PermissionEffect
}