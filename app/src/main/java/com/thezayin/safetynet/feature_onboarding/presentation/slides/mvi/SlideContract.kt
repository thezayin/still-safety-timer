package com.thezayin.safetynet.feature_onboarding.presentation.slides.mvi

import com.thezayin.safetynet.core.presentation.UiEffect
import com.thezayin.safetynet.core.presentation.UiIntent
import com.thezayin.safetynet.core.presentation.UiState

data class SlideState(
    val currentPage: Int = 0,
    val isLastPage: Boolean = false
) : UiState

sealed interface SlideIntent : UiIntent {
    data class OnPageScroll(val pageIndex: Int) : SlideIntent
    data object OnPrimaryActionClicked : SlideIntent
}

sealed interface SlideEffect : UiEffect {
    data class PagerScrollTo(val pageIndex: Int) : SlideEffect
    data object NavigateToProfileSetup : SlideEffect
}