package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.time.TimeProvider
import com.thezayin.safetynet.feature_timer.domain.model.TimerData
import com.thezayin.safetynet.feature_timer.domain.model.TimerPhase
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class ObserveTimerPhaseUseCaseTest {
    private val repository = mockk<TimerRepository>()
    private val timeProvider = mockk<TimeProvider>()
    private val observeTimerPhase = ObserveTimerPhaseUseCase(repository, timeProvider)

    @Test
    fun `when timer is inactive, phase is Idle`() = runTest {
        coEvery { repository.timerData } returns flowOf(TimerData(isActive = false, startTimestamp = 0, targetTimestamp = 0, durationHours = 48, lastCheckin = 0, history = emptyList(), streak = 0))

        val phase = observeTimerPhase().first()

        assertTrue(phase is TimerPhase.Idle)
    }

    @Test
    fun `when time is past target, phase is Abort`() = runTest {
        val now = 10000L
        val data = TimerData(isActive = true, startTimestamp = 0, targetTimestamp = now - 5000, durationHours = 48, lastCheckin = 0, history = emptyList(), streak = 0)

        coEvery { repository.timerData } returns flowOf(data)
        every { timeProvider.currentTimeMillis() } returns now

        val phase = observeTimerPhase().first()

        assertTrue(phase is TimerPhase.Abort)
    }
}