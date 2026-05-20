package com.thezayin.safetynet.core.analytics.event

/**
 * Valid trigger sources for emergency events to prevent stringly-typed errors.
 */
enum class TriggerSource {
    TIMER_EXPIRED,
    MANUAL
}

/**
 * Every trackable event in the app lives here as a sealed class.
 *
 * Rules:
 * - No raw strings anywhere in the codebase. Every event is a type.
 * - Parameters are strongly typed — no stringly-typed Maps passed around.
 * - Each event carries only the data that is meaningful for that event.
 * - Nothing outside this file decides what an event is named in Firebase —
 * that mapping lives solely in FirebaseAnalyticsTracker.
 *
 * Naming convention:
 * - Use past tense for things that happened: [TimerStarted], [CheckInCompleted]
 * - Use present tense for state: [EmergencyContactViewed]
 * - Group by feature using nested objects for readability.
 */
sealed class AnalyticsEvent {

    // ── Timer ─────────────────────────────────────────────────────────────────

    /**
     * User started the safety timer.
     * @param durationSeconds The full countdown duration the user selected.
     */
    data class TimerStarted(val durationSeconds: Long) : AnalyticsEvent()

    /**
     * User manually cancelled the timer before it expired.
     * @param remainingSeconds How much time was left when cancelled.
     */
    data class TimerCancelled(val remainingSeconds: Long) : AnalyticsEvent()

    /**
     * Timer ran to zero without a check-in — emergency flow triggered.
     * @param durationSeconds The original timer duration that expired.
     */
    data class TimerExpired(val durationSeconds: Long) : AnalyticsEvent()

    /**
     * User successfully checked in before the timer expired.
     * @param remainingSeconds How much time was left at check-in.
     */
    data class CheckInCompleted(val remainingSeconds: Long) : AnalyticsEvent()

    /**
     * User extended the timer mid-countdown.
     * @param addedSeconds    How many seconds were added.
     * @param remainingSeconds Remaining time at the point of extension.
     */
    data class TimerExtended(
        val addedSeconds: Long,
        val remainingSeconds: Long
    ) : AnalyticsEvent()

    // ── Emergency ─────────────────────────────────────────────────────────────

    /**
     * Emergency email was dispatched to contacts.
     * @param contactCount Number of contacts the email was sent to.
     * @param triggerSource What caused the send (Enum for type safety).
     */
    data class EmergencyEmailSent(
        val contactCount: Int,
        val triggerSource: TriggerSource
    ) : AnalyticsEvent()

    /**
     * Emergency email dispatch failed.
     * @param reason Short error description for debugging.
     */
    data class EmergencyEmailFailed(val reason: String) : AnalyticsEvent()

    /**
     * User manually triggered the emergency alert without waiting for expiry.
     */
    data object EmergencyTriggeredManually : AnalyticsEvent()

    // ── Emergency Contacts ────────────────────────────────────────────────────

    /** User added a new emergency contact. */
    data class EmergencyContactAdded(val totalContacts: Int) : AnalyticsEvent()

    /** User removed an emergency contact. */
    data class EmergencyContactRemoved(val totalContacts: Int) : AnalyticsEvent()

    /** User opened the emergency contacts screen. */
    data object EmergencyContactsViewed : AnalyticsEvent()

    // ── Onboarding ────────────────────────────────────────────────────────────

    /** User completed the onboarding flow. */
    data object OnboardingCompleted : AnalyticsEvent()

    /** User skipped onboarding. */
    data object OnboardingSkipped : AnalyticsEvent()

    // ── Permissions ───────────────────────────────────────────────────────────

    /**
     * A system permission was requested.
     * @param permission e.g. "POST_NOTIFICATIONS", "EXACT_ALARM"
     */
    data class PermissionRequested(val permission: String) : AnalyticsEvent()

    /**
     * A system permission was granted or denied.
     * @param permission e.g. "POST_NOTIFICATIONS"
     * @param granted    true if granted, false if denied.
     */
    data class PermissionResult(
        val permission: String,
        val granted: Boolean
    ) : AnalyticsEvent()

    // ── Ads ───────────────────────────────────────────────────────────────────

    /**
     * An ad was shown to the user.
     * @param adType e.g. "interstitial", "native_home", "banner", "rewarded"
     */
    data class AdShown(val adType: String) : AnalyticsEvent()

    /**
     * An ad failed to load.
     * @param adType  e.g. "interstitial"
     * @param reason  AdMob error message.
     */
    data class AdFailedToLoad(
        val adType: String,
        val reason: String
    ) : AnalyticsEvent()

    /**
     * User earned a reward from a rewarded ad.
     * @param adType Always "rewarded" — kept for consistency with [AdShown].
     */
    data class AdRewardEarned(val adType: String) : AnalyticsEvent()

    // ── App Lifecycle ─────────────────────────────────────────────────────────

    /** App was opened (cold start). */
    data object AppOpened : AnalyticsEvent()

    /**
     * App was opened via a notification tap.
     * @param notificationType e.g. "timer_warning", "emergency_alert"
     */
    data class AppOpenedFromNotification(val notificationType: String) : AnalyticsEvent()

    // ── Settings ──────────────────────────────────────────────────────────────

    /**
     * User changed a setting.
     * @param settingKey   e.g. "default_timer_duration", "notification_sound"
     * @param settingValue The new value as a string for logging.
     */
    data class SettingChanged(
        val settingKey: String,
        val settingValue: String
    ) : AnalyticsEvent()
}