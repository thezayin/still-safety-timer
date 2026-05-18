package com.thezayin.safetynet.feature_settings.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thezayin.safetynet.core.ads.manager.AdManager
import com.thezayin.safetynet.core.ads.model.AdType
import com.thezayin.safetynet.core.ads.native_ad.NativeAdComposable
import org.koin.compose.koinInject

@Composable
fun AdContainer(
    modifier: Modifier = Modifier,
    adManager: AdManager = koinInject()
) {
    // We use NATIVE_HOME as your primary native ad type,
    // ensuring it is properly disposed of via the NativeAdComposable
    NativeAdComposable(
        adType = AdType.NATIVE_HOME,
        adManager = adManager,
        modifier = modifier
    )
}