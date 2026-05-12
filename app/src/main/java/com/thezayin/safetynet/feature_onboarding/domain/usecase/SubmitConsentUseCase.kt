package com.thezayin.safetynet.feature_onboarding.domain.usecase

import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.feature_onboarding.domain.repository.OnboardingRepository

class SubmitConsentUseCase(
    private val repository: OnboardingRepository
) {
    suspend operator fun invoke(): DomainResult<Unit> {
        return repository.setConsentAccepted()
    }
}