package com.thezayin.safetynet.feature_onboarding.presentation.slides

import androidx.lifecycle.viewModelScope
import com.thezayin.safetynet.core.presentation.BaseViewModel
import com.thezayin.safetynet.feature_onboarding.domain.repository.OnboardingRepository
import com.thezayin.safetynet.feature_onboarding.presentation.slides.mvi.SlideEffect
import com.thezayin.safetynet.feature_onboarding.presentation.slides.mvi.SlideIntent
import com.thezayin.safetynet.feature_onboarding.presentation.slides.mvi.SlideState
import kotlinx.coroutines.launch

class SlideViewModel(
    private val repository: OnboardingRepository
) : BaseViewModel<SlideState, SlideIntent, SlideEffect>(
    initialState = SlideState()
) {
    override fun onIntent(intent: SlideIntent) {
        when (intent) {
            is SlideIntent.OnPageScroll -> {
                updateState {
                    copy(
                        currentPage = intent.pageIndex,
                        isLastPage = intent.pageIndex == 2
                    )
                }
            }
            is SlideIntent.OnPrimaryActionClicked -> {
                if (state.value.isLastPage) {
                    completeOnboarding()
                } else {
                    sendEffect(SlideEffect.PagerScrollTo(state.value.currentPage + 1))
                }
            }
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            repository.setSlidesCompleted().fold(
                onSuccess = {
                    sendEffect(SlideEffect.NavigateToProfileSetup)
                },
                onFailure = {
                    sendEffect(SlideEffect.NavigateToProfileSetup)
                }
            )
        }
    }
}