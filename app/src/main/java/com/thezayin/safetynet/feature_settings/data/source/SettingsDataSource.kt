package com.thezayin.safetynet.feature_settings.data.source

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SettingsDataSource {
    fun getSupportEmail(): Flow<String> = flowOf("theblue.locks@gmail.com")

    fun getPrivacyUrl(): Flow<String> =
        flowOf("https://stillsafety.blogspot.com/2026/05/privacy-policy.html")

    fun getTermsUrl(): Flow<String> =
        flowOf("https://stillsafety.blogspot.com/2026/05/terms-and-conditions.html")
}