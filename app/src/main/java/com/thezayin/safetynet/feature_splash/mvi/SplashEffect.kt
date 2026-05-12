package com.thezayin.safetynet.feature_splash.mvi

import com.thezayin.safetynet.core.presentation.UiEffect

sealed interface SplashEffect : UiEffect {
    data object NavigateToOnboarding : SplashEffect
    data object NavigateToProfileSetup : SplashEffect
    data object NavigateToDashboard : SplashEffect
}