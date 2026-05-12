package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.feedback.DeviceFeedbackService
import com.thezayin.safetynet.core.domain.notification.AppNotificationService
import com.thezayin.safetynet.feature_timer.domain.repository.TimerHardwareManager
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class AbortEmergencyUseCaseTest {
    private val repository = mockk<TimerRepository>()
    private val hardwareManager = mockk<TimerHardwareManager>(relaxed = true)
    private val feedbackService = mockk<DeviceFeedbackService>(relaxed = true)
    private val notificationService = mockk<AppNotificationService>(relaxed = true)

    private val abortEmergency = AbortEmergencyUseCase(
        repository,
        hardwareManager,
        feedbackService,
        notificationService
    )

    @Test
    fun `aborting emergency triggers all hardware and software kill switches`() = runTest {
        coEvery { repository.deactivateAndClear() } returns DomainResult.Success(Unit)

        val result = abortEmergency()

        coVerify { hardwareManager.cancelEmergencyWorker() }
        coVerify { hardwareManager.cancelExactAlarms() }
        coVerify { hardwareManager.stopSafetyService() }
        coVerify { feedbackService.stopEmergencyVibration() }
        coVerify { notificationService.cancelAll() }
        coVerify { repository.deactivateAndClear() }

        assertTrue(result is DomainResult.Success)
    }

    @Test
    fun `when repository fails to clear data, abort still returns failure result`() = runTest {
        // FIX: Removed <Unit> from Failure constructor call
        // Added explicit mockk<AppError>() to help the compiler see what is being passed
        val error = DomainResult.Failure(mockk<AppError>(relaxed = true))
        coEvery { repository.deactivateAndClear() } returns error

        val result = abortEmergency()

        assertTrue(result is DomainResult.Failure)
    }
}