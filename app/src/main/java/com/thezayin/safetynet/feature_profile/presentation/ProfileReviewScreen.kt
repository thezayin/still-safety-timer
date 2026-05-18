package com.thezayin.safetynet.feature_profile.presentation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thezayin.safetynet.MainActivity
import com.thezayin.safetynet.core.ads.banner.BannerAdComposable
import com.thezayin.safetynet.core.ads.manager.AdManager
import com.thezayin.safetynet.core.ads.model.AdType
import com.thezayin.safetynet.feature_profile.presentation.components.ProfileReviewContent
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileEffect
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileIntent
import org.koin.compose.koinInject

@Composable
fun ProfileReviewScreen(
    viewModel: ProfileViewModel,
    adManager: AdManager = koinInject(),
    onNavigateNext: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        adManager.load(AdType.REWARDED)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            if (effect is ProfileEffect.NavigateToDashboard) {
                if (activity != null) {
                    (activity as? MainActivity)?.suppressNextAppOpen()
                    adManager.show(AdType.REWARDED, activity) {
                        onNavigateNext()
                    }
                } else {
                    onNavigateNext()
                }
            }
        }
    }

    ProfileReviewContent(
        state = state,
        onBack = onNavigateBack,
        onConfirm = { viewModel.onIntent(ProfileIntent.OnConfirmProfileClicked) },
        bannerAdContent = {
            BannerAdComposable(adType = AdType.BANNER, adManager = adManager)
        })
}