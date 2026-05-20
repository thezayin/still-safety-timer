package com.thezayin.safetynet.core.ads.rewarded

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.thezayin.safetynet.core.ads.config.AdConfig
import com.thezayin.safetynet.core.ads.manager.AdHandler
import com.thezayin.safetynet.core.ads.state.AdState

class RewardedAdHandler(
    context: Context,
    private val onStateUpdate: (AdState<Any>) -> Unit
) : AdHandler {

    // Always use applicationContext to prevent memory leaks — never store an Activity reference.
    private val appContext = context.applicationContext

    private var rewardedAd: RewardedAd? = null

    override fun load() {
        RewardedAd.load(
            appContext,
            AdConfig.rewardedId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    onStateUpdate(AdState.Ready(true))
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    // 🔴 ANALYTICS FIX: Pass exact error code and mark stage as "load".
                    // Crucial for identifying if your rewarded fill rate is dropping due to Google
                    // (Error 3: No Fill) or due to the user (Error 2: Network).
                    onStateUpdate(AdState.Failed(error.message, error.code, "load"))
                }
            }
        )
    }

    override fun show(activity: Activity, onComplete: (rewardEarned: Boolean) -> Unit) {
        val ad = rewardedAd
        if (ad == null) {
            onComplete(false)
            return
        }

        // Captured in the dismiss callback so the reward decision and the
        // completion signal are always delivered together — no race possible.
        var rewardEarned = false

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                destroy()
                // Deliver the reward result that was set (or not) by the
                // reward callback below. This fires after onUserEarnedReward
                // if the user completes the ad, so rewardEarned is already
                // true by the time we get here.
                onComplete(rewardEarned)
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                // 🔴 ANALYTICS FIX: Notify the AdManager Gatekeeper of a silent "Show" failure.
                // This logs critical UX crashes where the user clicks a button to earn a reward
                // but the ad engine fails to render the video.
                onStateUpdate(AdState.Failed(error.message, error.code, "show"))
                destroy()
                onComplete(false)
            }
        }

        ad.show(activity) { _ ->
            // onUserEarnedReward fires before onAdDismissedFullScreenContent,
            // so setting this flag here is safe — it will be read in dismiss.
            rewardEarned = true
        }
    }

    override fun destroy() {
        rewardedAd?.fullScreenContentCallback = null
        rewardedAd = null
    }
}