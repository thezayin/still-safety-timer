package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.feedback.DeviceFeedbackService
import com.thezayin.safetynet.core.domain.notification.AppNotificationService
import com.thezayin.safetynet.core.domain.time.TimeProvider
import com.thezayin.safetynet.feature_timer.domain.repository.TimerHardwareManager
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import kotlinx.coroutines.flow.first

class CheckInUseCase(
    private val repository: TimerRepository,
    private val hardwareManager: TimerHardwareManager,
    private val timeProvider: TimeProvider,
    private val feedbackService: DeviceFeedbackService,
    private val notificationService: AppNotificationService
) {
    suspend operator fun invoke(): DomainResult<Unit> {
        val data = repository.timerData.first()
        if (!data.isActive) {
            return DomainResult.Failure(AppError.Timer.NotActive)
        }

        val now = timeProvider.currentTimeMillis()
       val newTarget = now + (data.durationHours * 3600000L)
        val updatedHistory = (data.history + now).takeLast(100)
        val dbResult1 = repository.recordCheckIn(now, updatedHistory)
        val dbResult2 = repository.setTimerActive(isActive = true, start = now, target = newTarget)
        if (dbResult1 is DomainResult.Failure) return dbResult1
        if (dbResult2 is DomainResult.Failure) return dbResult2
        hardwareManager.cancelEmergencyWorker()
        hardwareManager.scheduleExactAlarms(newTarget)
        hardwareManager.startSafetyService(newTarget)
        feedbackService.triggerCheckInHaptic()
        feedbackService.stopEmergencyVibration()
        notificationService.cancelZeroHourNotification()

        return DomainResult.Success(Unit)
    }
}