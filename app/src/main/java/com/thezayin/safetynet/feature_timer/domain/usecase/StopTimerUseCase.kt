package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.feedback.DeviceFeedbackService
import com.thezayin.safetynet.core.domain.notification.AppNotificationService
import com.thezayin.safetynet.feature_timer.domain.repository.TimerHardwareManager
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import kotlinx.coroutines.flow.first

class StopTimerUseCase(
    private val repository: TimerRepository,
    private val hardwareManager: TimerHardwareManager,
    private val feedbackService: DeviceFeedbackService,
    private val notificationService: AppNotificationService
) {
    suspend operator fun invoke(): DomainResult<Unit> {
        val data = repository.timerData.first()
        if (!data.isActive) {
            return DomainResult.Failure(AppError.Timer.NotActive)
        }
        hardwareManager.cancelExactAlarms()
        hardwareManager.cancelEmergencyWorker()
        hardwareManager.stopSafetyService()
        feedbackService.stopEmergencyVibration()
        notificationService.cancelAll()
        return repository.deactivateAndClear()
    }
}