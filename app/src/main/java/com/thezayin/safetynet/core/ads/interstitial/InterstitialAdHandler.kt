package com.thezayin.safetynet.core.ads.interstitial

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.thezayin.safetynet.core.ads.config.AdConfig
import com.thezayin.safetynet.core.ads.manager.AdHandler
import com.thezayin.safetynet.core.ads.state.AdState

class InterstitialAdHandler(
    context: Context,
    private val onStateUpdate: (AdState<Any>) -> Unit
) : AdHandler {

    // Always use applicationContext to prevent memory leaks — never store an Activity reference.
    private val appContext = context.applicationContext

    private var interstitialAd: InterstitialAd? = null

    override fun load() {
        InterstitialAd.load(
            appContext,
            AdConfig.interstitialId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    // Full-screen ads carry no UI payload — true is a simple sentinel value.
                    onStateUpdate(AdState.Ready(true))
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    // 🔴 ANALYTICS FIX: Pass exact error code and mark stage as "load".
                    // This prevents unfilterable string blobs from flooding your Firebase dashboard.
                    onStateUpdate(AdState.Failed(error.message, error.code, "load"))
                }
            }
        )
    }

    override fun show(activity: Activity, onComplete: (rewardEarned: Boolean) -> Unit) {
        val ad = interstitialAd
        if (ad == null) {
            onComplete(false)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                destroy()
                onComplete(false) // Interstitials never earn a reward
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                // 🔴 ANALYTICS FIX: Notify the AdManager Gatekeeper of a silent "Show" failure.
                // This logs critical UX crashes (when a user expects an ad but gets a black screen).
                onStateUpdate(AdState.Failed(error.message, error.code, "show"))
                destroy()
                onComplete(false)
            }

            // onAdShowedFullScreenContent — intentionally left blank.
            // AdManager's watchdog timer automatically handles the timeout case
            // if this fires but the dismiss callback never arrives.
        }

        ad.show(activity)
    }

    override fun destroy() {
        // Nullify the callback before dropping the reference to ensure the SDK lets go of it
        interstitialAd?.fullScreenContentCallback = null
        interstitialAd = null
    }
}