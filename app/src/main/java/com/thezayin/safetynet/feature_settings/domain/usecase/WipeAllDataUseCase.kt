package com.thezayin.safetynet.feature_settings.domain.usecase

import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.feature_onboarding.domain.repository.OnboardingRepository
import com.thezayin.safetynet.feature_profile.domain.repository.ProfileRepository
import com.thezayin.safetynet.feature_timer.domain.repository.TimerHardwareManager
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository

class WipeAllDataUseCase(
    private val timerRepository: TimerRepository,
    private val profileRepository: ProfileRepository,
    private val onboardingRepository: OnboardingRepository,
    private val hardwareManager: TimerHardwareManager
) {
    suspend operator fun invoke(): DomainResult<Unit> {
        hardwareManager.cancelExactAlarms()
        hardwareManager.cancelEmergencyWorker()
        hardwareManager.stopSafetyService()
        timerRepository.deactivateAndClear()
        profileRepository.clearAll()
        return DomainResult.Success(Unit)
    }
}