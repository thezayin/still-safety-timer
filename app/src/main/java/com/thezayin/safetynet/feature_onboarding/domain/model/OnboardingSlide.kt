package com.thezayin.safetynet.feature_onboarding.domain.model

import com.thezayin.safetynet.core.presentation.model.UiText

data class OnboardingSlide(
    val title: UiText,
    val description: UiText,
    val imageRes: Int
)