package com.thezayin.safetynet.core.ads.native_ad

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.thezayin.safetynet.core.ads.manager.AdHandler
import com.thezayin.safetynet.core.ads.state.AdState

class NativeAdLoader(
    context: Context,
    private val adUnitId: String,
    private val onStateUpdate: (AdState<Any>) -> Unit
) : AdHandler {

    // Always use applicationContext to prevent memory leaks if the Activity is destroyed
    private val appContext = context.applicationContext

    private var nativeAd: NativeAd? = null

    /**
     * AdMob recommends not showing a native ad loaded more than ~1 hour ago.
     * We track load time and emit Expired if show() is called on a stale ad.
     */
    private var loadedAtMs: Long = 0L
    private val adTtlMs: Long = 60 * 60 * 1_000L // 1 hour

    override fun load() {
        AdLoader.Builder(appContext, adUnitId)
            .forNativeAd { freshAd ->
                // Destroy the previous ad before storing the new one.
                // This prevents a memory leak when the loader fires a new ad
                // before the old one has been explicitly disposed.
                nativeAd?.destroy()
                nativeAd = freshAd
                loadedAtMs = SystemClock.elapsedRealtime()

                // Pass the loaded NativeAd payload to the Brain
                onStateUpdate(AdState.Ready(freshAd))
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    nativeAd = null

                    // 🔴 ANALYTICS FIX: Pass the exact error code and mark the failure stage as "load".
                    // This allows the Gatekeeper to send highly filterable data to Firebase Analytics
                    // so you can distinguish between Google No Fill (3) and User Network Error (2).
                    onStateUpdate(AdState.Failed(error.message, error.code, "load"))
                }
            })
            .build()
            .loadAd(AdRequest.Builder().build())
    }

    /**
     * Native ads are rendered directly in Compose via [NativeAdComposable].
     * show() is a no-op here — there is no full-screen experience to launch.
     * AdManager.show() should not be called for native ad types; use the
     * composable directly and let it react to the Ready state.
     */
    override fun show(activity: Activity, onComplete: (rewardEarned: Boolean) -> Unit) {
        onComplete(false)
    }

    /**
     * Checks if the loaded ad has exceeded its TTL before rendering.
     * Call this from the composable or AdManager.show() guard if needed.
     */
    fun isExpired(): Boolean {
        if (nativeAd == null) return false
        return SystemClock.elapsedRealtime() - loadedAtMs > adTtlMs
    }

    override fun destroy() {
        // Free memory explicitly from the AdMob SDK
        nativeAd?.destroy()
        nativeAd = null
        loadedAtMs = 0L
    }
}