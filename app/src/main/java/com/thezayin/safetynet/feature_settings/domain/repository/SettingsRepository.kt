package com.thezayin.safetynet.feature_settings.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val supportEmail: Flow<String>
    val privacyPolicyUrl: Flow<String>
    val termsUrl: Flow<String>
}