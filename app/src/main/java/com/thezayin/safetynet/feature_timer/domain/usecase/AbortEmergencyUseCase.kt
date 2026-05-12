package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.feedback.DeviceFeedbackService
import com.thezayin.safetynet.core.domain.notification.AppNotificationService
import com.thezayin.safetynet.feature_timer.domain.repository.TimerHardwareManager
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository

class AbortEmergencyUseCase(
    private val repository: TimerRepository,
    private val hardwareManager: TimerHardwareManager,
    private val feedbackService: DeviceFeedbackService,
    private val notificationService: AppNotificationService
) {
    suspend operator fun invoke(): DomainResult<Unit> {
        hardwareManager.cancelEmergencyWorker()
        hardwareManager.cancelExactAlarms()
        hardwareManager.stopSafetyService()

        feedbackService.stopEmergencyVibration()
        notificationService.cancelAll()

        return repository.deactivateAndClear()
    }
}