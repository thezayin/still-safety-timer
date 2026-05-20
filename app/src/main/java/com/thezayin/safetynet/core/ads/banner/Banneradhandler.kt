package com.thezayin.safetynet.core.ads.banner

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.thezayin.safetynet.core.ads.config.AdConfig
import com.thezayin.safetynet.core.ads.manager.AdHandler
import com.thezayin.safetynet.core.ads.state.AdState

/**
 * Manages a single [AdView] (banner) lifecycle.
 *
 * Banner ads differ fundamentally from full-screen ads:
 * - They render inline inside a [android.view.View] — there is no show() call.
 * - The [AdView] itself IS the deliverable. We emit it as the [AdState.Ready]
 * payload so the composable can drop it directly into the layout via AndroidView.
 * - The [AdView] must be paused/resumed with the Activity, and destroyed when
 * the composable leaves the composition. [AdManager.dispose()] handles destroy.
 *
 * [adSize] defaults to [AdSize.BANNER] (320×50 dp). Pass [AdSize.LARGE_BANNER],
 * [AdSize.MEDIUM_RECTANGLE], or an adaptive size if needed for a specific placement.
 */
class BannerAdHandler(
    context: Context,
    private val adSize: AdSize = AdSize.BANNER,
    private val onStateUpdate: (AdState<Any>) -> Unit
) : AdHandler {

    // Always use applicationContext to prevent memory leaks if the Activity is destroyed
    private val appContext = context.applicationContext

    // The AdView is the payload delivered to Compose. We hold a reference
    // so we can pause/resume it and destroy it cleanly.
    private var adView: AdView? = null

    override fun load() {
        // Prevent memory leaks: If an AdView already exists, destroy it before creating a new one.
        destroyAdView()

        val view = AdView(appContext).apply {
            adSize?.let { setAdSize(it) }
            adUnitId = AdConfig.bannerId

            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    // Pass the heavily configured AdView up to the Brain
                    onStateUpdate(AdState.Ready(this@apply))
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    // ANALYTICS FIX: Pass the exact error code and mark the failure stage as "load"
                    // This allows AdManager to send highly filterable data to Firebase.
                    onStateUpdate(AdState.Failed(error.message, error.code, "load"))
                }
            }
        }

        adView = view
        view.loadAd(AdRequest.Builder().build())
    }

    /**
     * Banners have no full-screen show experience.
     * The AdView renders itself once loaded — onComplete fires immediately.
     */
    override fun show(activity: Activity, onComplete: (rewardEarned: Boolean) -> Unit) {
        onComplete(false)
    }

    /**
     * Call when the host Activity/Fragment pauses to pause ad refresh.
     * Wire this up from your Activity's onPause or a Lifecycle observer.
     */
    fun pause() {
        adView?.pause()
    }

    /**
     * Call when the host Activity/Fragment resumes to resume ad refresh.
     * Wire this up from your Activity's onResume or a Lifecycle observer.
     */
    fun resume() {
        adView?.resume()
    }

    override fun destroy() {
        destroyAdView()
    }

    /**
     * Safely releases the AdMob SDK resources to prevent memory leaks.
     */
    private fun destroyAdView() {
        adView?.destroy()
        adView = null
    }
}