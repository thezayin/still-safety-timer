package com.thezayin.safetynet.feature_onboarding.domain.usecase

import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.feature_onboarding.domain.repository.OnboardingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CompleteSlidesUseCaseTest {

    private val repository = mockk<OnboardingRepository>()
    private val useCase = CompleteSlidesUseCase(repository)

    @Test
    fun `when slides are finished, repository returns success`() = runTest {
        // GIVEN
        coEvery { repository.setSlidesCompleted() } returns DomainResult.Success(Unit)

        // WHEN
        val result = useCase()

        // THEN
        coVerify(exactly = 1) { repository.setSlidesCompleted() }
        assertTrue(result is DomainResult.Success)
    }

    @Test
    fun `when repository fails, use case returns error result`() = runTest {
        // GIVEN
        // We simulate a disk failure using our mapped AppError system
        val simulatedError = AppError.System.DatabaseError
        coEvery { repository.setSlidesCompleted() } returns DomainResult.Failure(simulatedError)

        // WHEN
        val result = useCase()

        // THEN
        assertTrue(result is DomainResult.Failure)
        assertEquals(simulatedError, (result as DomainResult.Failure).error)
    }
}