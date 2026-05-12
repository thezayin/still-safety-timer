package com.thezayin.safetynet.feature_settings.domain.model

data class SettingsData(
    val userName: String = "",
    val contactName: String = "",
    val contactEmail: String = "",
    val checkInIntervalHours: Int = 24,
    val privacyPolicyUrl: String = "https://yourcompany.com/privacy",
    val termsUrl: String = "https://yourcompany.com/terms",
    val supportEmail: String = "support@yourcompany.com"
)