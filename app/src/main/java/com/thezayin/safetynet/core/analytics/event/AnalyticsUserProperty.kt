package com.thezayin.safetynet.core.analytics.event

/**
 * User-level properties that persist across sessions in Firebase Analytics.
 *
 * Unlike events (which are point-in-time), properties describe the user's
 * current state and are used for audience segmentation in Firebase.
 *
 * Rules:
 *  - Set these when the relevant state changes, not on every screen.
 *  - Values must be strings (Firebase requirement) — booleans become "true"/"false".
 *  - Firebase allows max 25 custom user properties per project.
 *
 * Each subclass carries:
 *  [key]   — the property name sent to Firebase (snake_case, max 24 chars)
 *  [value] — the property value (max 36 chars)
 */
sealed class AnalyticsUserProperty {
    abstract val key: String
    abstract val value: String

    /**
     * Whether the user has at least one emergency contact configured.
     * Useful for segmenting users who are "fully set up" vs not.
     */
    data class HasEmergencyContact(val has: Boolean) : AnalyticsUserProperty() {
        override val key = "has_emergency_contact"
        override val value = has.toString()
    }

    /**
     * How many emergency contacts the user has.
     */
    data class EmergencyContactCount(val count: Int) : AnalyticsUserProperty() {
        override val key = "emergency_contact_count"
        override val value = count.toString()
    }

    /**
     * The user's preferred default timer duration in seconds.
     * Set when user changes the default in settings.
     */
    data class DefaultTimerDuration(val seconds: Long) : AnalyticsUserProperty() {
        override val key = "default_timer_duration"
        override val value = seconds.toString()
    }

    /**
     * Whether the user completed onboarding.
     */
    data class OnboardingComplete(val complete: Boolean) : AnalyticsUserProperty() {
        override val key = "onboarding_complete"
        override val value = complete.toString()
    }

    /**
     * Whether the user has granted notification permission.
     */
    data class NotificationsEnabled(val enabled: Boolean) : AnalyticsUserProperty() {
        override val key = "notifications_enabled"
        override val value = enabled.toString()
    }

    /**
     * Whether the user has granted exact alarm permission.
     * Critical for reliability tracking — users without this are at higher
     * risk of the timer not firing on certain OEMs.
     */
    data class ExactAlarmEnabled(val enabled: Boolean) : AnalyticsUserProperty() {
        override val key = "exact_alarm_enabled"
        override val value = enabled.toString()
    }
}