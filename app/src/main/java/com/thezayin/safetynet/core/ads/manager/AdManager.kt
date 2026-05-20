package com.thezayin.safetynet.core.ads.manager

import android.app.Activity
import android.os.Handler
import android.os.Looper
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform
import com.thezayin.safetynet.core.ads.model.AdType
import com.thezayin.safetynet.core.ads.state.AdState
import com.thezayin.safetynet.core.analytics.AnalyticsManager
import com.thezayin.safetynet.core.analytics.event.AnalyticsEvent
import com.thezayin.safetynet.core.remoteconfig.RemoteConfigManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Central coordinator and Gatekeeper for all ad units in the app.
 *
 * Responsibilities:
 * - GATEKEEPER: Instantly blocks ads based on live Firebase Remote Config rules.
 * - STATE MACHINE: Owns the canonical [AdState] for each [AdType] and prevents double-loads.
 * - HEALING: Drives exponential backoff retry logic if an ad fails due to network issues.
 * - ANALYTICS: Automatically routes lifecycle events (Shows, Failures, Rewards) to Firebase.
 * - SAFETY: Uses watchdog timers to prevent the app from getting stuck if an ad crashes mid-show.
 */
class AdManager(
    private val handlers: Map<AdType, AdHandler>,
    private val analyticsManager: AnalyticsManager,
    private val remoteConfigManager: RemoteConfigManager
) {
    // Ensures all state mutations and watchdog timers run safely on the Main thread
    private val mainHandler = Handler(Looper.getMainLooper())

    // Holds the reactive state for every ad type defined in the app
    private val adStates: Map<AdType, MutableStateFlow<AdState<Any>>> =
        AdType.entries.associateWith { MutableStateFlow(AdState.Idle) }

    // Keeps track of active timers for full-screen ads to prevent infinite locks
    private val showingWatchdogs = mutableMapOf<AdType, Runnable>()

    // 🔴 MUTE SWITCH: Tracks if a giant ad is currently on the screen
    var isShowingFullScreenAd: Boolean = false
        private set


    fun initializeConsentAndAds(activity: Activity, onReady: () -> Unit) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        // For testing: Use DEBUG_GEOGRAPHY_EEA to force the popup to appear
        val params = com.google.android.ump.ConsentRequestParameters.Builder()
            // .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation.requestConsentInfoUpdate(activity, params, {
            if (consentInformation.isConsentFormAvailable) {
                UserMessagingPlatform.loadConsentForm(activity, { form ->
                    if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                        form.show(activity) { onReady() }
                    } else {
                        onReady()
                    }
                }, { onReady() })
            } else {
                onReady()
            }
        }, { onReady() })
    }

    // ── GATEKEEPER RULE ENGINE ────────────────────────────────────────────────

    /**
     * Evaluates the latest Remote Config JSON to decide if an ad is allowed to load or show.
     * This intercepts all ad requests, allowing us to kill bad ads instantly from the cloud.
     */
    private fun isAdEnabled(type: AdType): Boolean {
        // Grab the latest parsed config state
        val config = remoteConfigManager.configState.value.ads

        // Level 1: The Master Kill Switch
        // If false, NO ads will load or show anywhere in the app.
        if (!config.masterKillSwitch) return false

        // Level 2: Specific Placement Switches
        // Allows turning off a specific screen's ad without affecting the rest of the app.
        return when (type) {
            AdType.APP_OPEN -> config.placements.splashAppOpen
            AdType.NATIVE_HOME -> config.placements.homeNative
            // Make sure your new AdTypes (like NATIVE_CONSENT) are mapped here if added to config!
            AdType.REWARDED -> config.placements.resultRewarded
            AdType.BANNER -> config.placements.settingsBanner
            AdType.INTERSTITIAL -> true // Default to true, or add to Remote Config if needed
        }
    }

    // ── PUBLIC API ────────────────────────────────────────────────────────────

    /**
     * Exposes the state of an ad unit so the UI (Compose) can react instantly.
     */
    fun observeState(type: AdType): StateFlow<AdState<Any>> =
        requireNotNull(adStates[type]) {
            "No StateFlow for $type — did you add it to AdType without restarting?"
        }.asStateFlow()

    /**
     * Allows UI components to access specific handler functions (like pause/resume for Banners).
     */
    fun handlerFor(type: AdType): AdHandler? = handlers[type]

    /**
     * Attempts to fetch an ad from the network.
     */
    fun load(type: AdType) {
        // GATEKEEPER CHECK: Block load if disabled remotely
        if (!isAdEnabled(type)) {
            // Emitting Failed with a specific message lets the UI elegantly skip the ad
            updateState(type, AdState.Failed("Disabled by Remote Config", -1, "load"))
            return
        }

        // Prevent duplicate load requests
        val current = stateOf(type)
        if (current is AdState.Loading || current is AdState.Ready<*> || current is AdState.Showing) return

        val handler = handlers[type]
        if (handler == null) {
            updateState(type, AdState.Failed("No handler registered for $type", -1, "load"))
            return
        }

        // Transition to Loading and fire the network request
        updateState(type, AdState.Loading)
        handler.load()
    }

    /**
     * Attempts to display a full-screen ad.
     */
    fun show(
        type: AdType,
        activity: Activity,
        onComplete: (rewardEarned: Boolean) -> Unit = {}
    ) {
        // GATEKEEPER CHECK: Block show if rules changed remotely while the ad was sitting in memory
        if (!isAdEnabled(type)) {
            dispose(type)
            onComplete(false)
            return
        }

        // 🔴 AD-ON-AD BLOCKER: Prevent App Open Ad if a full screen ad is active or just closing
        if (type == AdType.APP_OPEN && isShowingFullScreenAd) {
            onComplete(false)
            return
        }

        // ATOMIC CHECK: Ensure we don't accidentally show two ads at the exact same millisecond
        val didTransition = transitionToShowing(type)
        if (!didTransition) {
            onComplete(false)
            return
        }

        // 🔴 MUTE SWITCH ON: We are officially showing a giant ad
        if (type == AdType.INTERSTITIAL || type == AdType.REWARDED) {
            isShowingFullScreenAd = true
        }

        // ANALYTICS: Track that the ad successfully hit the screen
        analyticsManager.track(AnalyticsEvent.AdShown(type.name.lowercase()))

        // Start the safety timer in case the ad crashes and never calls 'onDismiss'
        startShowingWatchdog(type)

        // Delegate the actual display logic to the specific Handler
        handlers[type]?.show(activity) { rewardEarned ->
            // 🔴 MUTE SWITCH OFF: The giant ad has fully closed
            if (type == AdType.INTERSTITIAL || type == AdType.REWARDED) {
                isShowingFullScreenAd = false
            }

            cancelShowingWatchdog(type)
            dispose(type)

            // ANALYTICS: Track if the user successfully completed the rewarded ad
            if (rewardEarned) {
                analyticsManager.track(AnalyticsEvent.AdRewardEarned(type.name.lowercase()))
            }
            onComplete(rewardEarned)
        } ?: run {
            // Fallback if handler is unexpectedly missing
            if (type == AdType.INTERSTITIAL || type == AdType.REWARDED) {
                isShowingFullScreenAd = false
            }
            cancelShowingWatchdog(type)
            dispose(type)
            onComplete(false)
        }
    }

    /**
     * Called by handlers to push a new state (Ready, Failed, etc.).
     * Also acts as the trigger for dynamic retry logic.
     */
    fun updateState(type: AdType, state: AdState<Any>) {
        adStates[type]?.value = state

        // If the ad failed, we need to track it and potentially try again
        if (state is AdState.Failed) {
            // ANALYTICS: Track exact error code and whether it failed on load or show
            analyticsManager.track(
                AnalyticsEvent.AdFailedToLoad(
                    adType = type.name.lowercase(),
                    reason = "[Code: ${state.code} | Stage: ${state.stage}] ${state.error}"
                )
            )

            // DYNAMIC RETRIES: Check Remote Config for the maximum allowed retries
            val maxRetries = remoteConfigManager.configState.value.ads.maxRetries
            if (state.attempt < maxRetries) {
                scheduleRetry(type, state.attempt)
            }
        }
    }

    /**
     * Destroys the ad object to free up memory and resets the state machine.
     */
    fun dispose(type: AdType) {
        handlers[type]?.destroy()
        adStates[type]?.value = AdState.Idle
    }

    // ── INTERNAL HELPERS ──────────────────────────────────────────────────────

    private fun stateOf(type: AdType): AdState<Any> =
        adStates[type]?.value ?: AdState.Idle

    /**
     * Atomically transitions an ad from Ready -> Showing.
     * @return true if successful, false if the ad was not Ready or is already Showing.
     */
    @Synchronized
    private fun transitionToShowing(type: AdType): Boolean {
        val flow = adStates[type] ?: return false
        return if (flow.value is AdState.Ready<*>) {
            flow.value = AdState.Showing
            true
        } else {
            false
        }
    }

    /**
     * Schedules a new load attempt using an exponential backoff formula.
     */
    private fun scheduleRetry(type: AdType, lastAttempt: Int) {
        // DYNAMIC DELAY: Calculate wait time based on Remote Config (e.g., 2000ms * 2^attempt)
        val baseDelayMs = remoteConfigManager.configState.value.ads.retryBaseDelayMs
        val delayMs = baseDelayMs * (1L shl lastAttempt)

        mainHandler.postDelayed({
            // Only fire the retry if the state is still Failed (user hasn't navigated away)
            val current = stateOf(type)
            if (current is AdState.Failed) {
                updateState(type, AdState.Loading)
                handlers[type]?.load()
            }
        }, delayMs)
    }

    /**
     * Starts a 60-second timer. If the ad hasn't reported back by then, we assume
     * the AdMob SDK crashed or the OS killed the process, so we forcibly reset the state.
     */
    private fun startShowingWatchdog(type: AdType) {
        val runnable = Runnable {
            if (stateOf(type) is AdState.Showing) {
                // Failsafe mute switch reset just in case
                if (type == AdType.INTERSTITIAL || type == AdType.REWARDED) {
                    isShowingFullScreenAd = false
                }
                dispose(type)
            }
        }
        showingWatchdogs[type] = runnable
        mainHandler.postDelayed(runnable, 60_000L)
    }

    private fun cancelShowingWatchdog(type: AdType) {
        showingWatchdogs.remove(type)?.let { mainHandler.removeCallbacks(it) }
    }
}