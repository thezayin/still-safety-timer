package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.time.TimeProvider
import com.thezayin.safetynet.feature_timer.domain.model.TimerData
import com.thezayin.safetynet.feature_timer.domain.repository.TimerHardwareManager
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RestoreTimerUseCaseTest {
    private val repository = mockk<TimerRepository>()
    private val hardwareManager = mockk<TimerHardwareManager>(relaxed = true)
    private val timeProvider = mockk<TimeProvider>()

    // FIX: Matches your 3-argument constructor (repository, hardwareManager, timeProvider)
    private val restoreTimer = RestoreTimerUseCase(repository, hardwareManager, timeProvider)

    @Test
    fun `when timer is active and time remains, restores alarms and service`() = runTest {
        val now = 1000L
        val targetTime = 5000L // 4 seconds in the future
        val data = TimerData(
            isActive = true,
            targetTimestamp = targetTime,
            // ... other default fields
            startTimestamp = 0, durationHours = 48, lastCheckin = 0, history = emptyList(), streak = 0
        )

        coEvery { repository.timerData } returns flowOf(data)
        every { timeProvider.currentTimeMillis() } returns now

        restoreTimer()

        // Verify standard restoration path
        coVerify { hardwareManager.scheduleExactAlarms(targetTime) }
        coVerify { hardwareManager.startSafetyService(targetTime) }
        coVerify(exactly = 0) { hardwareManager.enqueueEmergencyWorker() }
    }

    @Test
    fun `when timer is active but expired during reboot, enqueues emergency worker immediately`() = runTest {
        val now = 10000L
        val targetTime = 5000L // Expired 5 seconds ago
        val data = TimerData(
            isActive = true,
            targetTimestamp = targetTime,
            startTimestamp = 0, durationHours = 48, lastCheckin = 0, history = emptyList(), streak = 0
        )

        coEvery { repository.timerData } returns flowOf(data)
        every { timeProvider.currentTimeMillis() } returns now

        restoreTimer()

        // Verify emergency path
        coVerify { hardwareManager.enqueueEmergencyWorker() }
        coVerify(exactly = 0) { hardwareManager.scheduleExactAlarms(any()) }
        coVerify(exactly = 0) { hardwareManager.startSafetyService(any()) }
    }

    @Test
    fun `when timer is inactive, does nothing on reboot`() = runTest {
        coEvery { repository.timerData } returns flowOf(
            TimerData(isActive = false, startTimestamp = 0, targetTimestamp = 0, durationHours = 48, lastCheckin = 0, history = emptyList(), streak = 0)
        )

        restoreTimer()

        coVerify(exactly = 0) { hardwareManager.enqueueEmergencyWorker() }
        coVerify(exactly = 0) { hardwareManager.scheduleExactAlarms(any()) }
        coVerify(exactly = 0) { hardwareManager.startSafetyService(any()) }
    }
}