package com.thezayin.safetynet.core.ads.appopen

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.thezayin.safetynet.core.ads.config.AdConfig
import com.thezayin.safetynet.core.ads.manager.AdHandler
import com.thezayin.safetynet.core.ads.state.AdState

class AppOpenAdHandler(
    context: Context,
    private val onStateUpdate: (AdState<Any>) -> Unit
) : AdHandler {

    private val appContext = context.applicationContext

    private var appOpenAd: AppOpenAd? = null

    override fun load() {
        AppOpenAd.load(
            appContext,
            AdConfig.appOpenId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    onStateUpdate(AdState.Ready(true))
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    appOpenAd = null
                    // ANALYTICS FIX: Pass the exact error code and mark the failure stage as "load"
                    // This allows AdManager to send highly filterable data to Firebase.
                    onStateUpdate(AdState.Failed(error.message, error.code, "load"))
                }
            }
        )
    }

    override fun show(activity: Activity, onComplete: (rewardEarned: Boolean) -> Unit) {
        val ad = appOpenAd
        if (ad == null) {
            onComplete(false)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                destroy()
                // Note: The AdManager wrapper will intercept this callback to flip the mute switch
                onComplete(false)
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                // ANALYTICS FIX: Notify the AdManager that a "Show" failure occurred.
                // Previously, this failed silently. Now Firebase will log UX-critical rendering crashes.
                onStateUpdate(AdState.Failed(error.message, error.code, "show"))
                destroy()
                onComplete(false)
            }
        }

        ad.show(activity)
    }

    override fun destroy() {
        appOpenAd?.fullScreenContentCallback = null
        appOpenAd = null
    }
}