package com.thezayin.safetynet.feature_onboarding.domain.usecase

import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.feature_onboarding.domain.repository.OnboardingRepository

class CompleteSlidesUseCase(
    private val repository: OnboardingRepository
) {
    suspend operator fun invoke(): DomainResult<Unit> {
        return repository.setSlidesCompleted()
    }
}