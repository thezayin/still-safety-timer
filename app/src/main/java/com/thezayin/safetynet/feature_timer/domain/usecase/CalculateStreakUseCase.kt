package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.time.TimeProvider
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import kotlinx.coroutines.flow.first

class CalculateStreakUseCase(
    private val repository: TimerRepository,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(): Int {
        val data = repository.timerData.first()
        val history = data.history

        if (history.isEmpty()) return 0

        val now = timeProvider.currentTimeMillis()
        val maxGap = (data.durationHours * 3600000L) + 3600000L // Duration + 1 hr grace period

        if (now - history.last() > maxGap) return 0

        var streak = 0
        for (i in history.indices.reversed()) {
            if (i == 0) {
                streak++
                break
            }
            if (history[i] - history[i - 1] <= maxGap) {
                streak++
            } else {
                break
            }
        }
        return streak
    }
}