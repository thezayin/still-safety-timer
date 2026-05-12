package com.thezayin.safetynet.feature_settings.domain.usecase

import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository

class UpdateIntervalUseCase(
    private val timerRepository: TimerRepository
) {
    suspend operator fun invoke(hours: Int): DomainResult<Unit> {
        val validHours = if (hours == 48) 48 else 24
        return timerRepository.updateDuration(validHours)
    }
}