package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.notification.AppNotificationService
import com.thezayin.safetynet.feature_profile.domain.model.ProfileData
import com.thezayin.safetynet.feature_profile.domain.repository.ProfileRepository
import com.thezayin.safetynet.feature_timer.domain.repository.EmergencyDispatcher
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class SendEmergencyEmailUseCaseTest {
    private val profileRepository = mockk<ProfileRepository>()
    private val emergencyDispatcher = mockk<EmergencyDispatcher>()
    private val notificationService = mockk<AppNotificationService>(relaxed = true)

    private val sendEmail = SendEmergencyEmailUseCase(
        profileRepository,
        emergencyDispatcher,
        notificationService
    )

    @Test
    fun `when profile data is missing, returns Failure`() = runTest {
        // FIX: Use emptyFlow() instead of flowOf(null)
        // This satisfies the Flow<ProfileData> type requirement
        // but causes .firstOrNull() in the Use Case to return null.
        coEvery { profileRepository.profileData } returns emptyFlow<ProfileData>()

        val result = sendEmail()

        assertTrue(result is DomainResult.Failure && result.error is AppError.Alert.ProfileDataMissing)
    }

    @Test
    fun `when profile fields are blank, returns Failure`() = runTest {
        // Test for the second part of your IF statement (isBlank checks)
        val invalidProfile = ProfileData(
            userName = "",
            contactName = " ",
            contactEmail = ""
        )
        coEvery { profileRepository.profileData } returns flowOf(invalidProfile)

        val result = sendEmail()

        assertTrue(result is DomainResult.Failure && result.error is AppError.Alert.ProfileDataMissing)
    }
}