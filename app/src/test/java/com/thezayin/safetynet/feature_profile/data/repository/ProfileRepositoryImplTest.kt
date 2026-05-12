package com.thezayin.safetynet.feature_profile.data.repository

import app.cash.turbine.test
import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.security.CryptoService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProfileRepositoryImplTest {

    private lateinit var cryptoService: CryptoService
    private lateinit var repository: ProfileRepositoryImpl

    @Before
    fun setup() {
        cryptoService = mockk(relaxed = true)

        // Mock the initial decryption load when the repository is instantiated
        every { cryptoService.getAndDecrypt(any()) } returns DomainResult.Success("")

        repository = ProfileRepositoryImpl(cryptoService)
    }

    @Test
    fun `saveUserName encrypts data and updates StateFlow on success`() = runTest {
        // Arrange
        val newName = "Zayin"
        coEvery { cryptoService.encryptAndSave("pii_user_name", newName) } returns DomainResult.Success(Unit)

        repository.profileData.test {
            // Act
            val result = repository.saveUserName(newName)

            // Assert
            assertEquals(true, result.isSuccess)

            // Skip initial empty state, check the updated state
            skipItems(1)
            val updatedState = awaitItem()
            assertEquals(newName, updatedState.userName)

            coVerify(exactly = 1) { cryptoService.encryptAndSave("pii_user_name", newName) }
        }
    }

    @Test
    fun `saveContactEmail does not update StateFlow if encryption fails`() = runTest {
        // Arrange
        val email = "test@domain.com"
        // Create a generic mock of your base AppError
        val fakeError = mockk<AppError>()
        coEvery { cryptoService.encryptAndSave("pii_contact_email", email) } returns DomainResult.Failure(fakeError)

        repository.profileData.test {
            // Act
            val result = repository.saveContactEmail(email)

            // Assert
            assertEquals(true, result.isFailure)

            // StateFlow should only emit the initial empty state, nothing else
            val initialState = awaitItem()
            assertEquals("", initialState.contactEmail)
            expectNoEvents()
        }
    }

    @Test
    fun `clearAll deletes all keys and resets StateFlow`() = runTest {
        // Arrange
        coEvery { cryptoService.delete(any()) } returns DomainResult.Success(Unit)
        coEvery { cryptoService.encryptAndSave(any(), any()) } returns DomainResult.Success(Unit)

        // 1. Populate the repository with some dummy data first
        repository.saveUserName("Zayin")

        repository.profileData.test {
            // 2. The first item emitted is the current state containing "Zayin"
            val initialState = awaitItem()
            assertEquals("Zayin", initialState.userName)

            // Act
            repository.clearAll()

            // Assert: StateFlow registers the change from "Zayin" to "" and emits!
            val clearedState = awaitItem()
            assertEquals("", clearedState.userName)
            assertEquals("", clearedState.contactName)
            assertEquals("", clearedState.contactEmail)

            // Verify all 3 keys were deleted
            coVerify(exactly = 1) { cryptoService.delete("pii_user_name") }
            coVerify(exactly = 1) { cryptoService.delete("pii_contact_name") }
            coVerify(exactly = 1) { cryptoService.delete("pii_contact_email") }
        }
    }
}