package com.thezayin.safetynet.core.analytics.tracker

import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.thezayin.safetynet.core.analytics.event.AnalyticsEvent
import com.thezayin.safetynet.core.analytics.event.AnalyticsUserProperty
import com.thezayin.safetynet.core.domain.logger.LocalLogger

/**
 * Firebase Analytics implementation of [AnalyticsTracker].
 *
 * This is the ONLY class in the entire codebase allowed to import
 * Firebase Analytics. All Firebase-specific naming (event names,
 * parameter keys) lives here and nowhere else.
 */
@Suppress("DEPRECATION")
class FirebaseAnalyticsTracker(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val crashlytics: FirebaseCrashlytics,
    private val logger: LocalLogger
) : AnalyticsTracker {

    override fun track(event: AnalyticsEvent) {
        try {
            val (name, params) = event.toFirebaseEvent()
            firebaseAnalytics.logEvent(name, params)
        } catch (e: Exception) {
            // Log locally and report to Crashlytics
            logger.e(
                "FirebaseAnalyticsTracker",
                "Failed to track event: ${event::class.simpleName}",
                e
            )
            crashlytics.recordException(e)
        }
    }

    override fun setUserProperty(property: AnalyticsUserProperty) {
        try {
            firebaseAnalytics.setUserProperty(property.key, property.value)
        } catch (e: Exception) {
            logger.e("FirebaseAnalyticsTracker", "Failed to set user property: ${property.key}", e)
            crashlytics.recordException(e)
        }
    }

    // ── Event mapping ─────────────────────────────────────────────────────────

    private fun AnalyticsEvent.toFirebaseEvent(): Pair<String, Bundle?> = when (this) {

        // ── Timer ─────────────────────────────────────────────────────────────
        is AnalyticsEvent.TimerStarted -> "timer_started" to bundleOf(
            "duration_seconds" to durationSeconds
        )

        is AnalyticsEvent.TimerCancelled -> "timer_cancelled" to bundleOf(
            "remaining_seconds" to remainingSeconds
        )

        is AnalyticsEvent.TimerExpired -> "timer_expired" to bundleOf(
            "duration_seconds" to durationSeconds
        )

        is AnalyticsEvent.CheckInCompleted -> "check_in_completed" to bundleOf(
            "remaining_seconds" to remainingSeconds
        )

        is AnalyticsEvent.TimerExtended -> "timer_extended" to bundleOf(
            "added_seconds" to addedSeconds,
            "remaining_seconds" to remainingSeconds
        )

        // ── Emergency ─────────────────────────────────────────────────────────
        is AnalyticsEvent.EmergencyEmailSent -> "emergency_email_sent" to bundleOf(
            "contact_count" to contactCount,
            "trigger_source" to triggerSource.name.lowercase() // Converts Enum safely to "timer_expired" or "manual"
        )

        is AnalyticsEvent.EmergencyEmailFailed -> "emergency_email_failed" to bundleOf(
            "reason" to reason
        )

        is AnalyticsEvent.EmergencyTriggeredManually ->
            "emergency_triggered_manually" to null

        // ── Emergency Contacts ────────────────────────────────────────────────
        is AnalyticsEvent.EmergencyContactAdded -> "emergency_contact_added" to bundleOf(
            "total_contacts" to totalContacts
        )

        is AnalyticsEvent.EmergencyContactRemoved -> "emergency_contact_removed" to bundleOf(
            "total_contacts" to totalContacts
        )

        is AnalyticsEvent.EmergencyContactsViewed ->
            "emergency_contacts_viewed" to null

        // ── Onboarding ────────────────────────────────────────────────────────
        is AnalyticsEvent.OnboardingCompleted -> "onboarding_completed" to null
        is AnalyticsEvent.OnboardingSkipped -> "onboarding_skipped" to null

        // ── Permissions ───────────────────────────────────────────────────────
        is AnalyticsEvent.PermissionRequested -> "permission_requested" to bundleOf(
            "permission" to permission
        )

        is AnalyticsEvent.PermissionResult -> "permission_result" to bundleOf(
            "permission" to permission,
            "granted" to granted.toString()
        )

        // ── Ads ───────────────────────────────────────────────────────────────
        is AnalyticsEvent.AdShown -> "ad_shown" to bundleOf(
            "ad_type" to adType
        )

        is AnalyticsEvent.AdFailedToLoad -> "ad_failed_to_load" to bundleOf(
            "ad_type" to adType,
            "reason" to reason.take(100)
        )

        is AnalyticsEvent.AdRewardEarned -> "ad_reward_earned" to bundleOf(
            "ad_type" to adType
        )

        // ── App Lifecycle ─────────────────────────────────────────────────────
        is AnalyticsEvent.AppOpened -> "app_opened" to null

        is AnalyticsEvent.AppOpenedFromNotification -> "app_opened_from_notification" to bundleOf(
            "notification_type" to notificationType
        )

        // ── Settings ──────────────────────────────────────────────────────────
        is AnalyticsEvent.SettingChanged -> "setting_changed" to bundleOf(
            "setting_key" to settingKey,
            "setting_value" to settingValue.take(100)
        )
    }
}