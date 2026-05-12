package com.thezayin.safetynet.feature_splash.mvi

import com.thezayin.safetynet.core.presentation.UiIntent

sealed interface SplashIntent : UiIntent {
    data object CheckRouting : SplashIntent
}