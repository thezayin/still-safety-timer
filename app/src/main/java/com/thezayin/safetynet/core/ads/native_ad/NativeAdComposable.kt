package com.thezayin.safetynet.core.ads.native_ad

import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ads.manager.AdManager
import com.thezayin.safetynet.core.ads.model.AdType
import com.thezayin.safetynet.core.ads.state.AdState
import com.thezayin.safetynet.core.analytics.AnalyticsManager
import com.thezayin.safetynet.core.analytics.event.AnalyticsEvent
import org.koin.compose.koinInject

/**
 * Renders a Native Ad that is fully integrated with the [AdManager] state machine.
 *
 * Features:
 * - Shows an elegant shimmer placeholder while the ad is loading over the network.
 * - Collapses cleanly (zero height) on failure — no empty gap left behind.
 * - Disposes the [NativeAd] correctly when it leaves the composition to prevent memory leaks.
 * - Automatically logs an [AnalyticsEvent.AdShown] impression when rendered on screen.
 *
 * Usage:
 * ```
 * NativeAdComposable(
 * adType = AdType.NATIVE_HOME,
 * adManager = adManager
 * )
 * ```
 */
@Composable
fun NativeAdComposable(
    adType: AdType,
    adManager: AdManager,
    modifier: Modifier = Modifier,
    analyticsManager: AnalyticsManager = koinInject() // 🔴 Inject Analytics via Koin
) {
    // Reactively observe the current state of this specific ad placement
    val adState by adManager.observeState(adType).collectAsState()

    // Trigger load on entry and destroy on exit.
    // Keyed to adType so it behaves correctly if recycled in a LazyColumn.
    DisposableEffect(adType) {
        adManager.load(adType)
        onDispose { }
    }

    when (val state = adState) {
        is AdState.Ready<*> -> {
            // Safely cast the payload; if it fails, silently abort rendering.
            val nativeAd = state.payload as? NativeAd ?: return

            // 🔴 ANALYTICS FIX: Track the impression the moment the native ad is bound.
            // Keyed to [nativeAd] so this effect fires exactly once per unique loaded ad.
            LaunchedEffect(nativeAd) {
                analyticsManager.track(AnalyticsEvent.AdShown(adType.name.lowercase()))
            }

            AndroidView(
                modifier = modifier.fillMaxWidth(), factory = { context ->
                    // Inflate the XML layout required by AdMob
                    val adView = LayoutInflater.from(context)
                        .inflate(R.layout.ad_native_standard, null) as NativeAdView

                    // Map UI Components from the XML
                    adView.headlineView = adView.findViewById<TextView>(R.id.ad_headline)
                    adView.callToActionView = adView.findViewById<Button>(R.id.ad_call_to_action)
                    adView.iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)

                    // Bind Data from the Google SDK to our XML views
                    (adView.headlineView as TextView).text = nativeAd.headline
                    (adView.callToActionView as Button).text = nativeAd.callToAction ?: "View"

                    nativeAd.icon?.let {
                        (adView.iconView as ImageView).setImageDrawable(it.drawable)
                    }

                    // Crucial step: Tell the AdMob SDK which view holds the ad so clicks register
                    adView.setNativeAd(nativeAd)
                    adView
                })
        }

        is AdState.Loading -> {
            // Show the elegant shimmer placeholder while waiting for the network
            NativeAdShimmer(modifier = modifier)
        }

        else -> {
            // Failed, Expired, or Idle: Collapse the space so the user doesn't see a broken gap.
            // Using size(0.dp) ensures it has absolutely zero footprint in a Column.
            Spacer(modifier = Modifier.size(0.dp))
        }
    }
}

/**
 * Shimmer placeholder sized and styled to match the real Native Ad.
 * This prevents "layout shift" (UI jumping) when the ad suddenly loads.
 */
@Composable
fun NativeAdShimmer(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.05f, targetValue = 0.15f, animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0F1717)) // Matches 'Still' aesthetic background
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Fake App Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = alpha), RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))

            // Fake Text Lines
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(14.dp)
                        .background(Color.White.copy(alpha = alpha))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .background(Color.White.copy(alpha = alpha))
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            // Fake Call To Action Button
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(32.dp)
                    .background(
                        Color(0xFF4ECDC4).copy(alpha = alpha + 0.1f), // Slight tint for button
                        RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}