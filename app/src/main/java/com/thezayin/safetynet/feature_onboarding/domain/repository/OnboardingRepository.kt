package com.thezayin.safetynet.feature_onboarding.domain.repository

import com.thezayin.safetynet.core.domain.error.DomainResult
import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    val isSlidesCompleted: Flow<Boolean>
    val isConsentAccepted: Flow<Boolean>
    val isProfileComplete: Flow<Boolean>

    suspend fun setSlidesCompleted(): DomainResult<Unit>
    suspend fun setConsentAccepted(): DomainResult<Unit>
    suspend fun setProfileComplete(): DomainResult<Unit>
}