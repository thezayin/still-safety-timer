package com.thezayin.safetynet.core.analytics.tracker

import android.util.Log
import com.thezayin.safetynet.core.analytics.event.AnalyticsEvent
import com.thezayin.safetynet.core.analytics.event.AnalyticsUserProperty

/**
 * Debug-only [AnalyticsTracker] that prints every event to Logcat.
 *
 * This tracker is registered in Koin ONLY when [BuildConfig.DEBUG] is true,
 * so it is completely stripped from release builds — zero overhead in production.
 *
 * In Logcat, filter by tag "Analytics" to see all events in real time
 * as you test the app manually.
 *
 * Output format:
 *   [EVENT]    TimerStarted → durationSeconds=300
 *   [PROPERTY] HasEmergencyContact → has_emergency_contact = true
 */
class DebugAnalyticsTracker : AnalyticsTracker {

    override fun track(event: AnalyticsEvent) {
        val details = event.extractDetails()
        Log.d(TAG, "[EVENT]    ${event::class.simpleName} → $details")
    }

    override fun setUserProperty(property: AnalyticsUserProperty) {
        Log.d(TAG, "[PROPERTY] ${property::class.simpleName} → ${property.key} = ${property.value}")
    }

    /**
     * Extracts meaningful parameter details from each event for readable logging.
     * Uses reflection-free explicit mapping so the output is always predictable.
     */
    private fun AnalyticsEvent.extractDetails(): String = when (this) {
        is AnalyticsEvent.TimerStarted -> "durationSeconds=$durationSeconds"
        is AnalyticsEvent.TimerCancelled -> "remainingSeconds=$remainingSeconds"
        is AnalyticsEvent.TimerExpired -> "durationSeconds=$durationSeconds"
        is AnalyticsEvent.CheckInCompleted -> "remainingSeconds=$remainingSeconds"
        is AnalyticsEvent.TimerExtended -> "addedSeconds=$addedSeconds, remainingSeconds=$remainingSeconds"
        is AnalyticsEvent.EmergencyEmailSent -> "contactCount=$contactCount, triggerSource=$triggerSource"
        is AnalyticsEvent.EmergencyEmailFailed -> "reason=$reason"
        is AnalyticsEvent.EmergencyTriggeredManually -> "(no params)"
        is AnalyticsEvent.EmergencyContactAdded -> "totalContacts=$totalContacts"
        is AnalyticsEvent.EmergencyContactRemoved -> "totalContacts=$totalContacts"
        is AnalyticsEvent.EmergencyContactsViewed -> "(no params)"
        is AnalyticsEvent.OnboardingCompleted -> "(no params)"
        is AnalyticsEvent.OnboardingSkipped -> "(no params)"
        is AnalyticsEvent.PermissionRequested -> "permission=$permission"
        is AnalyticsEvent.PermissionResult -> "permission=$permission, granted=$granted"
        is AnalyticsEvent.AdShown -> "adType=$adType"
        is AnalyticsEvent.AdFailedToLoad -> "adType=$adType, reason=$reason"
        is AnalyticsEvent.AdRewardEarned -> "adType=$adType"
        is AnalyticsEvent.AppOpened -> "(no params)"
        is AnalyticsEvent.AppOpenedFromNotification -> "notificationType=$notificationType"
        is AnalyticsEvent.SettingChanged -> "key=$settingKey, value=$settingValue"
    }

    private companion object {
        const val TAG = "Analytics"
    }
}