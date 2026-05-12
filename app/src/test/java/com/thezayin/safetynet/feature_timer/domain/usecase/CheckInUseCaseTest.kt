package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.feedback.DeviceFeedbackService
import com.thezayin.safetynet.core.domain.notification.AppNotificationService
import com.thezayin.safetynet.core.domain.time.TimeProvider
import com.thezayin.safetynet.feature_timer.domain.model.TimerData
import com.thezayin.safetynet.feature_timer.domain.repository.TimerHardwareManager
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CheckInUseCaseTest {
    private val repository = mockk<TimerRepository>(relaxed = true)
    private val hardwareManager = mockk<TimerHardwareManager>(relaxed = true)
    private val timeProvider = mockk<TimeProvider>()

    // FIX: Add the missing mocks
    private val feedbackService = mockk<DeviceFeedbackService>(relaxed = true)
    private val notificationService = mockk<AppNotificationService>(relaxed = true)

    // FIX: Pass all 5 parameters to the constructor
    private val checkIn = CheckInUseCase(
        repository,
        hardwareManager,
        timeProvider,
        feedbackService,
        notificationService
    )

    @Test
    fun `checking in updates repository with new target timestamp`() = runTest {
        val now = 1000L
        val data = TimerData(
            isActive = true,
            startTimestamp = 0,
            targetTimestamp = 5000,
            durationHours = 24,
            lastCheckin = 0,
            history = emptyList(),
            streak = 0
        )

        coEvery { repository.timerData } returns flowOf(data)
        every { timeProvider.currentTimeMillis() } returns now

        checkIn()

        val expectedTarget = now + (24 * 3600000L)

        // 1. Verify DB update
        coVerify { repository.setTimerActive(true, any(), expectedTarget) }

        // 2. Verify Alarm scheduling
        coVerify { hardwareManager.scheduleExactAlarms(expectedTarget) }

        // 3. Verify Haptic Feedback (Based on your error log)
        verify { feedbackService.triggerCheckInHaptic() }
    }
}