package com.thezayin.safetynet.feature_profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thezayin.safetynet.core.ads.banner.BannerAdComposable
import com.thezayin.safetynet.core.ads.manager.AdManager
import com.thezayin.safetynet.core.ads.model.AdType
import com.thezayin.safetynet.feature_profile.presentation.components.NameSetupContent
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileEffect
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileIntent
import org.koin.compose.koinInject

@Composable
fun NameSetupScreen(
    viewModel: ProfileViewModel,
    adManager: AdManager = koinInject(),
    onNavigateNext: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            if (effect is ProfileEffect.NavigateToContactName) {
                onNavigateNext()
            }
        }
    }

    NameSetupContent(
        state = state,
        onNameChange = { viewModel.onIntent(ProfileIntent.OnUserNameChanged(it)) },
        onBack = onNavigateBack,
        onContinue = { viewModel.onIntent(ProfileIntent.OnSaveUserNameClicked) },
        bannerAdContent = {
            BannerAdComposable(adType = AdType.BANNER, adManager = adManager)
        }
    )
}