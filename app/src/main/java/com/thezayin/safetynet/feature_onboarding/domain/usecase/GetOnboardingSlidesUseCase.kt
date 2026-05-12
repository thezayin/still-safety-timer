package com.thezayin.safetynet.feature_onboarding.domain.usecase

import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.presentation.model.UiText
import com.thezayin.safetynet.feature_onboarding.domain.model.OnboardingSlide

class GetOnboardingSlidesUseCase {
    operator fun invoke(): List<OnboardingSlide> = listOf(
        OnboardingSlide(
            title = UiText.StringResource(),
            description = UiText.StringResource(),
            imageRes = R.drawable.ic_aura_mint
        ),
        OnboardingSlide(
            title = UiText.StringResource(),
            description = UiText.StringResource(),
            imageRes = R.drawable.ic_aura_coral
        )
    )
}