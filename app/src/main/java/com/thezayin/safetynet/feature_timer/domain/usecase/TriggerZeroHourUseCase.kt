package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.feedback.DeviceFeedbackService
import com.thezayin.safetynet.core.domain.notification.AppNotificationService
import com.thezayin.safetynet.feature_timer.domain.repository.TimerHardwareManager

class TriggerZeroHourUseCase(
    private val hardwareManager: TimerHardwareManager,
    private val feedbackService: DeviceFeedbackService,
    private val notificationService: AppNotificationService
) {
    operator fun invoke() {
        hardwareManager.enqueueEmergencyWorker()
        notificationService.showZeroHourAlertOnce()
        feedbackService.triggerEmergencyVibration()
    }
}