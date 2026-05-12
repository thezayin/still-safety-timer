package com.thezayin.safetynet.feature_onboarding.presentation.permissions

import androidx.lifecycle.viewModelScope
import com.thezayin.safetynet.core.presentation.BaseViewModel
import com.thezayin.safetynet.feature_onboarding.domain.permission.PermissionChecker
import com.thezayin.safetynet.feature_onboarding.presentation.permissions.mvi.PermissionEffect
import com.thezayin.safetynet.feature_onboarding.presentation.permissions.mvi.PermissionIntent
import com.thezayin.safetynet.feature_onboarding.presentation.permissions.mvi.PermissionState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PermissionViewModel(
    private val permissionChecker: PermissionChecker
) : BaseViewModel<PermissionState, PermissionIntent, PermissionEffect>(
    initialState = PermissionState()
) {

    init {
        permissionChecker.observePermissionStatus()
            .onEach { status -> updateState { copy(status = status) } }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: PermissionIntent) {
        when (intent) {
            PermissionIntent.RefreshStatus -> permissionChecker.checkAllPermissions()
            PermissionIntent.RequestNotification -> sendEffect(PermissionEffect.TriggerNotificationPopup)
            PermissionIntent.RequestAlarm -> sendEffect(PermissionEffect.OpenAlarmSettings)
            PermissionIntent.OnContinueClicked -> {
                if (state.value.status.isAllGranted) {
                    sendEffect(PermissionEffect.NavigateToProfile)
                }
            }
        }
    }
}