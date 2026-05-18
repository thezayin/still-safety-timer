package com.thezayin.safetynet.core.ads.banner

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.thezayin.safetynet.core.ads.config.AdConfig
import com.thezayin.safetynet.core.ads.manager.AdManager
import com.thezayin.safetynet.core.ads.model.AdType
import com.thezayin.safetynet.core.analytics.AnalyticsManager
import com.thezayin.safetynet.core.analytics.event.AnalyticsEvent
import org.koin.compose.koinInject

/**
 * Renders a banner ad using the local Compose Activity Context.
 * Banners bypass the background AdManager singleton to prevent Context leaks
 * and '#004 Webview Destroyed' errors from the Google Ads SDK.
 */
@Composable
fun BannerAdComposable(
    adType: AdType,
    adManager: AdManager, // Kept so you don't have to change your caller code!
    modifier: Modifier = Modifier,
    adSize: AdSize = AdSize.BANNER,
    analyticsManager: AnalyticsManager = koinInject()
) {
    var isLoading by remember { mutableStateOf(true) }
    var isFailed by remember { mutableStateOf(false) }

    if (isFailed) {
        // Failed: collapse cleanly to zero height
        Spacer(modifier = Modifier.size(0.dp))
        return
    }

    Box(modifier = modifier.fillMaxWidth()) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                // The 'context' here is the true Activity Context provided by Compose
                AdView(context).apply {
                    setAdSize(adSize)
                    adUnitId = AdConfig.bannerId

                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            isLoading = false
                            // Log the impression the moment it successfully renders
                            analyticsManager.track(AnalyticsEvent.AdShown(adType.name.lowercase()))
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            isLoading = false
                            isFailed = true
                        }
                    }

                    // Fire the request immediately upon creation
                    loadAd(AdRequest.Builder().build())
                }
            }
        )

        // Show the shimmer overlay while we wait for the network response
        if (isLoading) {
            BannerAdShimmer(adSize = adSize)
        }
    }
}

@Composable
private fun BannerAdShimmer(
    modifier: Modifier = Modifier,
    adSize: AdSize = AdSize.BANNER
) {
    val shimmerHeight = adSize.height.dp

    val transition = rememberInfiniteTransition(label = "banner_shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "banner_shimmer_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(shimmerHeight)
            .padding(horizontal = 8.dp)
            .background(Color.White.copy(alpha = alpha))
    )
}