package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.notification.AppNotificationService
import com.thezayin.safetynet.feature_profile.domain.repository.ProfileRepository
import com.thezayin.safetynet.feature_timer.domain.repository.EmergencyDispatcher
import kotlinx.coroutines.flow.firstOrNull

class SendEmergencyEmailUseCase(
    private val profileRepository: ProfileRepository,
    private val emergencyDispatcher: EmergencyDispatcher,
    private val notificationService: AppNotificationService
) {
    suspend operator fun invoke(): DomainResult<Unit> {
        val profileData = profileRepository.profileData.firstOrNull()

        if (profileData == null ||
            profileData.userName.isBlank() ||
            profileData.contactName.isBlank() ||
            profileData.contactEmail.isBlank()
        ) {
            return DomainResult.Failure(AppError.Alert.ProfileDataMissing)
        }

        val result = emergencyDispatcher.sendEmailAlert(
            toEmail = profileData.contactEmail,
            toName = profileData.contactName,
            userName = profileData.userName
        )

        if (result is DomainResult.Success) {
            notificationService.showEmergencyDispatchedNotification()
        }

        return result
    }
}