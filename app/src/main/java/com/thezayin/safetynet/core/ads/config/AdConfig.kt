package com.thezayin.safetynet.core.ads.config

object AdConfig {

    /**
     * Flip to false before a production release.
     * Consider driving this from BuildConfig.DEBUG instead:
     *   var isTestMode = BuildConfig.DEBUG
     */
    var isTestMode = true

    /** Maximum load attempts before giving up and emitting AdState.Failed. */
    const val MAX_RETRIES = 3

    /**
     * Delay in milliseconds between retry attempts.
     * Attempt 1 → 2s, attempt 2 → 4s, attempt 3 → 8s (exponential backoff).
     */
    const val RETRY_BASE_DELAY_MS = 2_000L

    /**
     * Maximum time a full-screen ad is allowed to remain in Showing state
     * before AdManager forcibly resets it to Idle. Guards against callbacks
     * that never fire (e.g. activity destroyed mid-show).
     */
    const val MAX_SHOW_DURATION_MS = 60_000L

    // ── Ad unit IDs ───────────────────────────────────────────────────────────
    // Replace the right-hand strings with your real unit IDs before release.

    val nativeId: String
        get() = if (isTestMode)
            "ca-app-pub-3940256099942544/2247696110"
        else
            "ca-app-pub-2913057115284606/3754818887"

    val interstitialId: String
        get() = if (isTestMode)
            "ca-app-pub-3940256099942544/1033173712"
        else
            "ca-app-pub-2913057115284606/2633308906"

    val rewardedId: String
        get() = if (isTestMode)
            "ca-app-pub-3940256099942544/5224354917"
        else
            "ca-app-pub-2913057115284606/1076684651"

    val appOpenId: String
        get() = if (isTestMode)
            "ca-app-pub-3940256099942544/9257395921"
        else
            "ca-app-pub-2913057115284606/5917947797"

    val bannerId: String
        get() = if (isTestMode)
            "ca-app-pub-3940256099942544/6300978111"
        else
            "ca-app-pub-2913057115284606/8764821578"

    // NativeResult ID — uncomment when you wire up its handler in adModule.kt
    // val nativeResultId: String
    //     get() = if (isTestMode) "ca-app-pub-3940256099942544/2247696110" else "YOUR_PROD_NATIVE_RESULT_ID"
}