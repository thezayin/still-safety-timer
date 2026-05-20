package com.thezayin.safetynet.core.remoteconfig.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class AppConfig(
    @SerialName("ads") val ads: AdsConfig = AdsConfig(),
    @SerialName("safety_mechanics") val safetyMechanics: SafetyMechanicsConfig = SafetyMechanicsConfig(),
    @SerialName("lifecycle") val lifecycle: LifecycleConfig = LifecycleConfig()
)

@Keep
@Serializable
data class AdsConfig(
    @SerialName("master_kill_switch") val masterKillSwitch: Boolean = true,
    @SerialName("placements") val placements: AdPlacements = AdPlacements(),
    @SerialName("max_retries") val maxRetries: Int = 3,
    @SerialName("retry_base_delay_ms") val retryBaseDelayMs: Long = 2000L,
    @SerialName("splash_timeout_ms") val splashTimeoutMs: Long = 3000L
)

@Keep
@Serializable
data class AdPlacements(
    @SerialName("splash_app_open") val splashAppOpen: Boolean = true,
    @SerialName("home_native") val homeNative: Boolean = true,
    @SerialName("result_rewarded") val resultRewarded: Boolean = true,
    @SerialName("settings_banner") val settingsBanner: Boolean = false
)

@Keep
@Serializable
data class SafetyMechanicsConfig(
    @SerialName("is_email_dispatch_enabled") val isEmailDispatchEnabled: Boolean = true,
    @SerialName("max_timer_duration_hours") val maxTimerDurationHours: Int = 72,
    @SerialName("emergency_contact_limit_free") val emergencyContactLimitFree: Int = 3
)

@Keep
@Serializable
data class LifecycleConfig(
    @SerialName("min_required_version_code") val minRequiredVersionCode: Int = 1,
    @SerialName("force_update_message") val forceUpdateMessage: String = "Please update the app to continue."
)
