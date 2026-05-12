package com.thezayin.safetynet.feature_splash

import androidx.lifecycle.viewModelScope
import com.thezayin.safetynet.core.presentation.BaseViewModel
import com.thezayin.safetynet.feature_onboarding.domain.repository.OnboardingRepository
import com.thezayin.safetynet.feature_profile.domain.repository.ProfileRepository
import com.thezayin.safetynet.feature_splash.mvi.SplashEffect
import com.thezayin.safetynet.feature_splash.mvi.SplashIntent
import com.thezayin.safetynet.feature_splash.mvi.SplashState
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(
    private val onboardingRepository: OnboardingRepository,
    private val profileRepository: ProfileRepository
) : BaseViewModel<SplashState, SplashIntent, SplashEffect>(
    initialState = SplashState()
) {

    init {
        onIntent(SplashIntent.CheckRouting)
    }

    override fun onIntent(intent: SplashIntent) {
        when (intent) {
            SplashIntent.CheckRouting -> handleRouting()
        }
    }

    private fun handleRouting() {
        viewModelScope.launch {
            val timerDeferred = async { delay(2000) }
            val slidesDeferred = async { onboardingRepository.isSlidesCompleted.first() }
            val consentDeferred = async { onboardingRepository.isConsentAccepted.first() }
            val profileDeferred = async { profileRepository.profileData.first() }
            timerDeferred.await()
            val isSlidesDone = slidesDeferred.await()
            val isConsentDone = consentDeferred.await()
            val profile = profileDeferred.await()
            when {
                !isSlidesDone || !isConsentDone -> {
                    sendEffect(SplashEffect.NavigateToOnboarding)
                }

                profile.userName.isBlank() || profile.contactEmail.isBlank() -> {
                    sendEffect(SplashEffect.NavigateToProfileSetup)
                }

                else -> {
                    sendEffect(SplashEffect.NavigateToDashboard)
                }
            }
        }
    }
}