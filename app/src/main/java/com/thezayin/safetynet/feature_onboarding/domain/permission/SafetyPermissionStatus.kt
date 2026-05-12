package com.thezayin.safetynet.feature_onboarding.domain.permission

data class SafetyPermissionStatus(
    val notifications: Boolean = false,
    val exactAlarms: Boolean = false
) {
    val isAllGranted: Boolean = notifications && exactAlarms
}