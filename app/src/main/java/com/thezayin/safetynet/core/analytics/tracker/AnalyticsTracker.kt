package com.thezayin.safetynet.core.analytics.tracker

import com.thezayin.safetynet.core.analytics.event.AnalyticsEvent
import com.thezayin.safetynet.core.analytics.event.AnalyticsUserProperty

/**
 * Contract every analytics tracker must fulfil.
 *
 * Trackers are responsible for:
 *  - Accepting a strongly-typed [AnalyticsEvent] and sending it to their
 *    respective destination (Firebase, Logcat, etc.).
 *  - Mapping event types to destination-specific names and parameters.
 *  - Never throwing exceptions — failures must be swallowed silently so
 *    analytics never crashes the app.
 *
 * Trackers must NOT:
 *  - Know about other trackers.
 *  - Hold any state.
 *  - Import anything from outside the analytics package.
 */
interface AnalyticsTracker {

    /**
     * Track a single event.
     * Implementations must catch all exceptions internally.
     */
    fun track(event: AnalyticsEvent)

    /**
     * Set a persistent user property.
     * Implementations must catch all exceptions internally.
     */
    fun setUserProperty(property: AnalyticsUserProperty)
}