package com.thezayin.safetynet.feature_onboarding.domain.permission

import kotlinx.coroutines.flow.Flow

interface PermissionChecker {
    fun checkAllPermissions(): SafetyPermissionStatus
    fun observePermissionStatus(): Flow<SafetyPermissionStatus>
}