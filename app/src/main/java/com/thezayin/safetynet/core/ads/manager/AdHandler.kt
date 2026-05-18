package com.thezayin.safetynet.core.ads.manager

import android.app.Activity

/**
 * Contract every ad handler must fulfil.
 *
 * Handlers are responsible for:
 *  - Talking to the AdMob SDK (load / show / destroy).
 *  - Reporting state changes back via [onStateUpdate] (injected at construction).
 *  - Never holding a reference to an Activity longer than a single show() call.
 *
 * Handlers must NOT:
 *  - Drive retry logic (that lives in AdManager).
 *  - Hold Application or Activity context beyond what the SDK requires.
 */
interface AdHandler {

    /**
     * Begin loading an ad. Implementations must call [onStateUpdate] with
     * AdState.Ready on success or AdState.Failed on failure.
     */
    fun load()

    /**
     * Show a loaded ad.
     *
     * @param activity   The foreground activity. Never store this reference.
     * @param onComplete Called when the ad is dismissed or fails to show.
     *                   [rewardEarned] is true only for rewarded ads where
     *                   the user completed the reward condition; always false
     *                   for all other ad types.
     */
    fun show(activity: Activity, onComplete: (rewardEarned: Boolean) -> Unit = {})

    /**
     * Release the SDK ad object and free memory. Safe to call multiple times.
     */
    fun destroy()
}