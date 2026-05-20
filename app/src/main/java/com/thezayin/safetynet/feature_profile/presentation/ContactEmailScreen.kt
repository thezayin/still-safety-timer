package com.thezayin.safetynet.feature_profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thezayin.safetynet.core.ads.banner.BannerAdComposable
import com.thezayin.safetynet.core.ads.manager.AdManager
import com.thezayin.safetynet.core.ads.model.AdType
import com.thezayin.safetynet.feature_profile.presentation.components.ContactEmailContent
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileEffect
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileIntent
import org.koin.compose.koinInject

@Composable
fun ContactEmailScreen(
    viewModel: ProfileViewModel,
    onNavigateNext: () -> Unit,
    onNavigateBack: () -> Unit,
    adManager: AdManager = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            if (effect is ProfileEffect.NavigateToDashboard) {
                onNavigateNext()
            }
        }
    }

    ContactEmailContent(
        state = state,
        onEmailChange = { viewModel.onIntent(ProfileIntent.OnContactEmailChanged(it)) },
        onBack = onNavigateBack,
        onContinue = { viewModel.onIntent(ProfileIntent.OnConfirmProfileClicked) },
        bannerAdContent = {
            BannerAdComposable(adType = AdType.BANNER, adManager = adManager)
        })
}