package com.thezayin.safetynet.core.ads.model

/**
 * Every ad placement in the app.
 *
 * IMPORTANT: Every value here MUST have a corresponding handler registered
 * in adModule.kt. Unregistered types will emit AdState.Failed immediately
 * rather than silently hanging on Loading forever.
 *
 * Add new placements here AND in adModule.kt at the same time.
 */
enum class AdType {
    NATIVE_HOME,
    INTERSTITIAL,
    REWARDED,
    APP_OPEN,
    BANNER,

    // Add NATIVE_RESULT here when you wire up its handler in adModule.kt
}