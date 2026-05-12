package com.thezayin.safetynet.feature_timer.presentation

import app.cash.turbine.test
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.monitor.PowerMonitor
import com.thezayin.safetynet.feature_timer.domain.model.TimerData
import com.thezayin.safetynet.feature_timer.domain.model.TimerPhase
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import com.thezayin.safetynet.feature_timer.domain.usecase.*
import com.thezayin.safetynet.feature_timer.presentation.contract.TimerIntent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // Mocking all 9 dependencies
    private val repository = mockk<TimerRepository>(relaxed = true)
    private val startTimerUseCase = mockk<StartTimerUseCase>()
    private val stopTimerUseCase = mockk<StopTimerUseCase>()
    private val checkInUseCase = mockk<CheckInUseCase>()
    private val abortEmergencyUseCase = mockk<AbortEmergencyUseCase>()
    private val updateDurationUseCase = mockk<UpdateTimerDurationUseCase>()
    private val observeTimerPhase = mockk<ObserveTimerPhaseUseCase>()
    private val calculateStreak = mockk<CalculateStreakUseCase>()
    private val powerMonitor = mockk<PowerMonitor>(relaxed = true)

    private lateinit var viewModel: TimerViewModel

    // Persistent data flow
    private val timerDataFlow = MutableStateFlow(
        TimerData(
            isActive = false,
            startTimestamp = 0L,
            targetTimestamp = 0L,
            durationHours = 48,
            lastCheckin = 0L,
            history = emptyList(),
            streak = 0
        )
    )

    // UI state flows
    private val phaseFlow = MutableStateFlow<TimerPhase>(TimerPhase.Idle)
    private val batteryFlow = MutableStateFlow(false)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Stubs for initialization
        every { observeTimerPhase() } returns phaseFlow
        every { powerMonitor.batterySaverState } returns batteryFlow
        every { repository.timerData } returns timerDataFlow
        coEvery { calculateStreak() } returns 0

        viewModel = TimerViewModel(
            repository, startTimerUseCase, stopTimerUseCase, checkInUseCase,
            abortEmergencyUseCase, updateDurationUseCase, observeTimerPhase,
            calculateStreak, powerMonitor
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when background timer hits Teal phase, UI updates to Active state`() = runTest {
        viewModel.state.test {
            // Initial state check
            assertEquals(TimerPhase.Idle, awaitItem().phase)

            // FIX: Using 'Active' instead of 'Running' to match your model
            phaseFlow.value = TimerPhase.Active(remainingMillis = 3600000L)

            val state = awaitItem()
            assertTrue(state.phase is TimerPhase.Active)
        }
    }

    @Test
    fun `when StartTimer intent is handled, loading state is toggled correctly`() = runTest {
        coEvery { startTimerUseCase() } returns DomainResult.Success(Unit)

        viewModel.state.test {
            skipItems(1) // Skip the idle state
            viewModel.onIntent(TimerIntent.StartTimer)

            // Verify loading sequence
            assertTrue(awaitItem().isLoading) // Toggles ON
            assertEquals(false, awaitItem().isLoading) // Toggles OFF
        }

        coVerify { startTimerUseCase() }
    }

    @Test
    fun `when background timer hits Zero Hour, UI updates to Abort state`() = runTest {
        viewModel.state.test {
            skipItems(1)

            // Testing the Critical/Red phase
            phaseFlow.value = TimerPhase.Critical(remainingMillis = 600000L)
            assertTrue(awaitItem().phase is TimerPhase.Critical)

            // Testing the 60s Abort window
            phaseFlow.value = TimerPhase.Abort(abortSecondsRemaining = 45)
            val state = awaitItem()
            assertTrue(state.phase is TimerPhase.Abort)
        }
    }
}