package com.thezayin.safetynet.core.ads.state

/**
 * Represents every possible lifecycle state of a single ad unit.
 *
 * State machine:
 *   Idle -> Loading -> Ready -> Showing -> Idle
 *                   -> Failed -> Idle (after retry exhausted)
 *                   -> Expired -> Idle (ad TTL exceeded before show)
 */
sealed class AdState<out T> {

    /** No ad loaded. Safe to call load(). */
    object Idle : AdState<Nothing>()

    /** AdMob request in-flight. Do not call load() again. */
    object Loading : AdState<Nothing>()

    /**
     * Ad loaded and ready.
     * [payload] is the SDK object (NativeAd) for inline ads,
     * or simply `true` for full-screen ads that don't need a payload.
     */
    data class Ready<T>(val payload: T) : AdState<T>()

    /** Full-screen ad is currently visible. Prevents double-show. */
    object Showing : AdState<Nothing>()

    /**
     * Load failed.
     * [error]   — AdMob error message for logging.
     * [attempt] — which retry attempt this was (1-based). Used by AdManager.
     */
    data class Failed(
        val error: String, val code: Int = -1, // Added error code
        val stage: String = "load", // "load" or "show"
        val attempt: Int = 1
    ) : AdState<Nothing>()

    /**
     * Ad loaded successfully but expired before it could be shown.
     * AdMob native ads are valid for ~1 hour. AdManager emits this
     * state when it detects a stale Ready ad on show().
     */
    object Expired : AdState<Nothing>()
}