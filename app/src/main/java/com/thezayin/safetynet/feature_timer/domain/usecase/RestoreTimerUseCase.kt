package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.time.TimeProvider
import com.thezayin.safetynet.feature_timer.domain.repository.TimerHardwareManager
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import kotlinx.coroutines.flow.first

class RestoreTimerUseCase(
    private val repository: TimerRepository,
    private val hardwareManager: TimerHardwareManager,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke() {
        val data = repository.timerData.first()

        if (data.isActive) {
            val now = timeProvider.currentTimeMillis()

            if (now >= data.targetTimestamp) {
                hardwareManager.enqueueEmergencyWorker()
            } else {
                hardwareManager.scheduleExactAlarms(data.targetTimestamp)
                hardwareManager.startSafetyService(data.targetTimestamp)
            }
        }
    }
}