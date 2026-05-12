package com.thezayin.safetynet.feature_onboarding.presentation.consent

import androidx.lifecycle.viewModelScope
import com.thezayin.safetynet.core.presentation.BaseViewModel
import com.thezayin.safetynet.feature_onboarding.domain.repository.OnboardingRepository
import com.thezayin.safetynet.feature_onboarding.presentation.consent.mvi.ConsentEffect
import com.thezayin.safetynet.feature_onboarding.presentation.consent.mvi.ConsentIntent
import com.thezayin.safetynet.feature_onboarding.presentation.consent.mvi.ConsentState
import kotlinx.coroutines.launch

class ConsentViewModel(
    private val repository: OnboardingRepository
) : BaseViewModel<ConsentState, ConsentIntent, ConsentEffect>(
    initialState = ConsentState()
) {
    override fun onIntent(intent: ConsentIntent) {
        when (intent) {
            is ConsentIntent.OnToggleAcceptance -> {
                updateState { copy(isAccepted = intent.isChecked) }
            }

            is ConsentIntent.OnSubmitClicked -> executeSubmit()
            is ConsentIntent.OnDeclineClicked -> sendEffect(ConsentEffect.ExitApp)
        }
    }

    private fun executeSubmit() {
        viewModelScope.launch {
            updateState { copy(isSubmitting = true) }

            repository.setConsentAccepted().fold(
                onSuccess = {
                    updateState { copy(isSubmitting = false) }
                    sendEffect(ConsentEffect.NavigateToPermissions)
                },
                onFailure = { error ->
                    updateState { copy(isSubmitting = false) }
                    sendEffect(ConsentEffect.ShowError(error))
                }
            )
        }
    }
}