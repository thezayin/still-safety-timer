package com.thezayin.safetynet.feature_profile.domain.usecase

import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.feature_profile.domain.repository.ProfileRepository

class SaveProfileDataUseCase(
    private val profileRepository: ProfileRepository
) {
    suspend fun saveUserName(name: String): DomainResult<Unit> =
        profileRepository.saveUserName(name.trim())

    suspend fun saveContactName(name: String): DomainResult<Unit> =
        profileRepository.saveContactName(name.trim())

    suspend fun saveContactEmail(email: String): DomainResult<Unit> =
        profileRepository.saveContactEmail(email.trim())
}