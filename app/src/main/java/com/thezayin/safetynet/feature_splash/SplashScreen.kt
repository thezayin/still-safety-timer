package com.thezayin.safetynet.feature_splash

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thezayin.safetynet.core.ads.manager.AdManager
import com.thezayin.safetynet.core.ads.model.AdType
import com.thezayin.safetynet.core.ads.native_ad.NativeAdComposable
import com.thezayin.safetynet.core.presentation.util.ObserveEffect
import com.thezayin.safetynet.feature_splash.component.SplashContent
import com.thezayin.safetynet.feature_splash.mvi.SplashEffect
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = koinViewModel(),
    adManager: AdManager = koinInject(), // 🔴 Inject the AdManager
    onNavigate: (SplashEffect) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val activity = LocalContext.current as? Activity

    // Intercept the navigation effect from the ViewModel
    ObserveEffect(viewModel.effect) { effect ->
        if (activity != null) {
            // Try to show the App Open Ad
            // If the ad isn't ready or fails, this callback fires instantly anyway
            adManager.show(AdType.APP_OPEN, activity) {
                // Navigate ONLY after the ad is dismissed (or if it failed)
                onNavigate(effect)
            }
        } else {
            // Fallback if activity context is missing
            onNavigate(effect)
        }
    }

    SplashContent(
        isLoading = state.isLoading,
        nativeAdContent = {
            // 🔴 Render the Native Ad at the bottom.
            // Note: Make sure AdType.NATIVE_SPLASH (or whichever name you use) is in your AdType enum!
            NativeAdComposable(
                adType = AdType.NATIVE_HOME,
                adManager = adManager
            )
        }
    )
}