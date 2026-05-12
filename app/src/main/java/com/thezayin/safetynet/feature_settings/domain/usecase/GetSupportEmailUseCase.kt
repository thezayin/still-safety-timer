package com.thezayin.safetynet.feature_settings.domain.usecase

import com.thezayin.safetynet.feature_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class GetSupportEmailUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(userName: String): Pair<String, String> {
        val email = settingsRepository.supportEmail.first()
        val subject = "[Still Support] Safety Protocol Query from $userName"
        return email to subject
    }
}