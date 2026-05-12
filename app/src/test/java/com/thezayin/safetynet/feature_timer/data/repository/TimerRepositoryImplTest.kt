package com.thezayin.safetynet.feature_timer.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TimerRepositoryImplTest {

    private val dataStore = mockk<DataStore<Preferences>>()
    private val logger = mockk<LocalLogger>(relaxed = true)

    // FIX 1: Don't initialize here. Use lateinit.
    private lateinit var repository: TimerRepositoryImpl

    @Before
    fun setUp() {
        // FIX 2: Stub the 'data' property BEFORE creating the repository
        // This prevents the "no answer found" crash during initialization
        every { dataStore.data } returns flowOf(emptyPreferences())

        repository = TimerRepositoryImpl(dataStore, logger)
    }

    @Test
    fun `timerData flow maps preferences correctly`() = runTest {
        // Your test code here...
    }
}