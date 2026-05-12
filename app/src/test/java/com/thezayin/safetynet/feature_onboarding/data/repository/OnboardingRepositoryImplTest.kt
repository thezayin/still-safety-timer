package com.thezayin.safetynet.feature_onboarding.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesOf
import app.cash.turbine.test
import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class OnboardingRepositoryImplTest {

    private lateinit var mockDataStore: DataStore<Preferences>

    // Replicating the keys from the repository for testing
    private val SLIDES_COMPLETED_KEY = booleanPreferencesKey("slides_completed")
    private val CONSENT_ACCEPTED_KEY = booleanPreferencesKey("consent_accepted")

    @Before
    fun setup() {
        // We only initialize the mock here.
        // We initialize the repository inside the tests AFTER configuring the mock!
        mockDataStore = mockk(relaxed = true)
    }

    // --- Tests for Slides Completed ---

    @Test
    fun `isSlidesCompleted emits true when preference is saved`() = runTest {
        // Arrange
        val prefs = preferencesOf(SLIDES_COMPLETED_KEY to true)
        every { mockDataStore.data } returns flowOf(prefs)

        val repository = OnboardingRepositoryImpl(mockDataStore)

        // Act & Assert
        repository.isSlidesCompleted.test {
            assertEquals(true, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `isSlidesCompleted emits false when preference is missing`() = runTest {
        // Arrange
        every { mockDataStore.data } returns flowOf(emptyPreferences())

        val repository = OnboardingRepositoryImpl(mockDataStore)

        // Act & Assert
        repository.isSlidesCompleted.test {
            assertEquals(false, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `isSlidesCompleted catches IOException and emits false`() = runTest {
        // Arrange: Simulate a file read error
        every { mockDataStore.data } returns flow { throw IOException("Disk read error") }

        val repository = OnboardingRepositoryImpl(mockDataStore)

        // Act & Assert
        repository.isSlidesCompleted.test {
            assertEquals(false, awaitItem()) // Because it catches IOException and emits emptyPreferences
            awaitComplete()
        }
    }

    @Test
    fun `setSlidesCompleted returns Success when DataStore write succeeds`() = runTest {
        // Arrange
        val repository = OnboardingRepositoryImpl(mockDataStore)
        coEvery { mockDataStore.updateData(any()) } returns emptyPreferences()

        // Act
        val result = repository.setSlidesCompleted()

        // Assert
        assertTrue(result is DomainResult.Success)
    }

    @Test
    fun `setSlidesCompleted returns DatabaseError when DataStore write fails`() = runTest {
        // Arrange
        val repository = OnboardingRepositoryImpl(mockDataStore)
        coEvery { mockDataStore.updateData(any()) } throws Exception("DataStore write failed")

        // Act
        val result = repository.setSlidesCompleted()

        // Assert
        assertTrue(result is DomainResult.Failure)
        assertEquals(AppError.System.DatabaseError, (result as DomainResult.Failure).error)
    }

    // --- Tests for Consent Accepted ---

    @Test
    fun `isConsentAccepted emits true when preference is saved`() = runTest {
        // Arrange
        val prefs = preferencesOf(CONSENT_ACCEPTED_KEY to true)
        every { mockDataStore.data } returns flowOf(prefs)

        val repository = OnboardingRepositoryImpl(mockDataStore)

        // Act & Assert
        repository.isConsentAccepted.test {
            assertEquals(true, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `isConsentAccepted emits false when preference is missing`() = runTest {
        // Arrange
        every { mockDataStore.data } returns flowOf(emptyPreferences())

        val repository = OnboardingRepositoryImpl(mockDataStore)

        // Act & Assert
        repository.isConsentAccepted.test {
            assertEquals(false, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `setConsentAccepted returns Success when DataStore write succeeds`() = runTest {
        // Arrange
        val repository = OnboardingRepositoryImpl(mockDataStore)
        coEvery { mockDataStore.updateData(any()) } returns emptyPreferences()

        // Act
        val result = repository.setConsentAccepted()

        // Assert
        assertTrue(result is DomainResult.Success)
    }

    @Test
    fun `setConsentAccepted returns DatabaseError when DataStore write fails`() = runTest {
        // Arrange
        val repository = OnboardingRepositoryImpl(mockDataStore)
        coEvery { mockDataStore.updateData(any()) } throws Exception("DataStore write failed")

        // Act
        val result = repository.setConsentAccepted()

        // Assert
        assertTrue(result is DomainResult.Failure)
        assertEquals(AppError.System.DatabaseError, (result as DomainResult.Failure).error)
    }
}