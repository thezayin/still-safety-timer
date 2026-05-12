package com.thezayin.safetynet.feature_onboarding.domain.usecase

import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.feature_onboarding.domain.repository.OnboardingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class SubmitConsentUseCaseTest {

    private val repository = mockk<OnboardingRepository>()
    private val useCase = SubmitConsentUseCase(repository)

    @Test
    fun `when consent is submitted, repository updates correctly`() = runTest {
        // GIVEN
        coEvery { repository.setConsentAccepted() } returns DomainResult.Success(Unit)

        // WHEN
        val result = useCase()

        // THEN
        coVerify(exactly = 1) { repository.setConsentAccepted() }
        assertTrue(result is DomainResult.Success)
    }
}