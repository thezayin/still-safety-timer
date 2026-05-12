package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository

class UpdateTimerDurationUseCase(
    private val repository: TimerRepository
) {
    suspend operator fun invoke(hours: Int): DomainResult<Unit> {
        val safeHours = hours.coerceIn(1, 168)
        return repository.updateDuration(safeHours)
    }
}