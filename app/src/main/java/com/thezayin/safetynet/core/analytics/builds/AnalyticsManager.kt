package com.thezayin.safetynet.core.analytics

import com.thezayin.safetynet.core.analytics.event.AnalyticsEvent
import com.thezayin.safetynet.core.analytics.event.AnalyticsUserProperty
import com.thezayin.safetynet.core.analytics.tracker.AnalyticsTracker

/**
 * Single public API for the entire analytics system.
 *
 * This is the ONLY class any ViewModel, Worker, Screen, or AdManager
 * should import from the analytics package. Everything else is internal.
 *
 * Usage from anywhere in the app:
 * ```
 * analyticsManager.track(AnalyticsEvent.TimerStarted(durationSeconds = 300))
 * analyticsManager.setUserProperty(AnalyticsUserProperty.HasEmergencyContact(true))
 * ```
 *
 * Responsibilities:
 *  - Accept events and properties from any caller on any thread.
 *  - Fan out to every registered [AnalyticsTracker].
 *  - Never throw — analytics must never affect app stability.
 *
 * [trackers] is injected by Koin. In debug builds it contains both
 * [FirebaseAnalyticsTracker] and [DebugAnalyticsTracker].
 * In release builds it contains only [FirebaseAnalyticsTracker].
 */
class AnalyticsManager(
    private val trackers: List<AnalyticsTracker>
) {

    /**
     * Track an event. Safe to call from any thread — UI, ViewModel,
     * WorkManager worker, or background coroutine.
     *
     * FirebaseAnalytics handles its own thread-safety internally.
     */
    fun track(event: AnalyticsEvent) {
        trackers.forEach { tracker ->
            runCatching { tracker.track(event) }
            // runCatching swallows any exception a tracker forgot to handle.
            // Analytics failures must never propagate to the caller.
        }
    }

    /**
     * Set a persistent user property. Safe to call from any thread.
     * Properties persist across sessions in Firebase Analytics.
     */
    fun setUserProperty(property: AnalyticsUserProperty) {
        trackers.forEach { tracker ->
            runCatching { tracker.setUserProperty(property) }
        }
    }
}