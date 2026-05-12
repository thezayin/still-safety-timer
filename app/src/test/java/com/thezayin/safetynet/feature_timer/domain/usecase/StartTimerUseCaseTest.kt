package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.monitor.PowerMonitor
import com.thezayin.safetynet.core.domain.time.TimeProvider
import com.thezayin.safetynet.feature_timer.domain.model.TimerData
import com.thezayin.safetynet.feature_timer.domain.repository.TimerHardwareManager
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class StartTimerUseCaseTest {
    private val repository = mockk<TimerRepository>(relaxed = true)
    private val hardwareManager = mockk<TimerHardwareManager>(relaxed = true)
    private val timeProvider = mockk<TimeProvider>()
    private val powerMonitor = mockk<PowerMonitor>()
    private val startTimer = StartTimerUseCase(repository, hardwareManager, timeProvider, powerMonitor)

    @Test
    fun `when battery saver is enabled, start fails`() = runTest {
        every { powerMonitor.isBatterySaverEnabled() } returns true

        val result = startTimer()

        assertTrue(result is DomainResult.Failure && result.error is AppError.System.PowerSaverActive)
    }

    @Test
    fun `when alarm scheduling fails, state is rolled back in database`() = runTest {
        // Setup
        every { powerMonitor.isBatterySaverEnabled() } returns false
        coEvery { repository.timerData } returns flowOf(TimerData(isActive = false, startTimestamp = 0, targetTimestamp = 0, durationHours = 48, lastCheckin = 0, history = emptyList(), streak = 0))
        every { timeProvider.currentTimeMillis() } returns 1000L

        // Mock hardware failure
        coEvery { hardwareManager.scheduleExactAlarms(any()) } returns DomainResult.Failure(AppError.Permission.ExactAlarmDenied)

        val result = startTimer()

        // Verify rollback
        assertTrue(result is DomainResult.Failure)
        coVerify { repository.deactivateAndClear() }
    }
}