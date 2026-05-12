package com.thezayin.safetynet.feature_timer.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import com.thezayin.safetynet.feature_timer.domain.model.TimerData
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import com.thezayin.safetynet.feature_timer.domain.usecase.SendEmergencyEmailUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

class EmergencyAlertWorkerTest : KoinTest {

    private val repository = mockk<TimerRepository>()
    private val sendEmailUseCase = mockk<SendEmergencyEmailUseCase>()
    private val logger = mockk<LocalLogger>(relaxed = true)

    private val context = mockk<Context>(relaxed = true)
    private val params = mockk<WorkerParameters>(relaxed = true)

    @Before
    fun setup() {
        // Start Koin to satisfy the 'by inject()' calls inside the Worker
        startKoin {
            modules(module {
                single { repository }
                single { sendEmailUseCase }
                single { logger }
            })
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `when timer is deactivated, doWork returns success`() = runTest {
        coEvery { repository.timerData } returns flowOf(
            TimerData(isActive = false, startTimestamp = 0, targetTimestamp = 0, durationHours = 48, lastCheckin = 0, history = emptyList(), streak = 0)
        )

        val worker = EmergencyAlertWorker(context, params)
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `when network fails, doWork returns retry`() = runTest {
        coEvery { repository.timerData } returns flowOf(
            TimerData(isActive = true, startTimestamp = 0, targetTimestamp = 0, durationHours = 48, lastCheckin = 0, history = emptyList(), streak = 0)
        )

        // FIX: Instantiate the Data Class mock properly
        // If NetworkFailure has parameters, mockk<AppError.Alert.NetworkFailure>(relaxed = true)
        // handles it regardless of constructor.
        val networkError = mockk<AppError.Alert.NetworkFailure>(relaxed = true)
        coEvery { sendEmailUseCase() } returns DomainResult.Failure(networkError)

        val worker = EmergencyAlertWorker(context, params)
        val result = worker.doWork()

        // Verify it triggers Result.retry() per your code logic
        assertEquals(ListenableWorker.Result.retry(), result)
    }
}