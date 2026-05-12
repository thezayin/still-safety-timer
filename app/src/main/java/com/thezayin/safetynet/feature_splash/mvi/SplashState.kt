package com.thezayin.safetynet.feature_splash.mvi

import com.thezayin.safetynet.core.presentation.UiState

data class SplashState(
    val isLoading: Boolean = true
) : UiState