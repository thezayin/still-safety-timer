package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.feedback.DeviceFeedbackService
import com.thezayin.safetynet.core.domain.notification.AppNotificationService
import com.thezayin.safetynet.feature_timer.domain.repository.TimerHardwareManager
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TriggerZeroHourUseCaseTest {
    private val hardwareManager = mockk<TimerHardwareManager>(relaxed = true)
    private val feedbackService = mockk<DeviceFeedbackService>(relaxed = true)
    private val notificationService = mockk<AppNotificationService>(relaxed = true)

    // FIX: Matches your 3-argument constructor
    private val triggerZeroHour = TriggerZeroHourUseCase(
        hardwareManager,
        feedbackService,
        notificationService
    )

    @Test
    fun `triggering zero hour starts vibration, shows notification, and enqueues SOS worker`() = runTest {
        triggerZeroHour()

        // 1. Verify the 60-second SOS countdown worker is started
        coVerify { hardwareManager.enqueueEmergencyWorker() }

        // 2. Verify the high-priority "Zero Hour" notification is shown
        verify { notificationService.showZeroHourNotification() }

        // 3. Verify the physical vibration starts (using your method name)
        verify { feedbackService.triggerEmergencyVibration() }
    }
}