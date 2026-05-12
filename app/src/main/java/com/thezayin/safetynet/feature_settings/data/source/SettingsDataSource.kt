package com.thezayin.safetynet.feature_settings.data.source

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SettingsDataSource {
    fun getSupportEmail(): Flow<String> = flowOf("support@thezayin.com")

    fun getPrivacyUrl(): Flow<String> = flowOf("https://thezayin.com/privacy")

    fun getTermsUrl(): Flow<String> = flowOf("https://thezayin.com/terms")
}