package com.thezayin.safetynet.feature_settings.data.repository

import com.thezayin.safetynet.feature_settings.data.source.SettingsDataSource
import com.thezayin.safetynet.feature_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    dataSource: SettingsDataSource
) : SettingsRepository {

    override val supportEmail: Flow<String> = dataSource.getSupportEmail()
    override val privacyPolicyUrl: Flow<String> = dataSource.getPrivacyUrl()
    override val termsUrl: Flow<String> = dataSource.getTermsUrl()
}