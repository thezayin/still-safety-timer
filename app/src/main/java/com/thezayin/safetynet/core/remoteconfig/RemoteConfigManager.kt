package com.thezayin.safetynet.core.remoteconfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import com.thezayin.safetynet.core.remoteconfig.model.AppConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class RemoteConfigManager(
    private val remoteConfig: FirebaseRemoteConfig,
    private val json: Json,
    private val logger: LocalLogger
) {
    // Expose the config reactively. Initialize it with a parsed version of the fallback.
    private val _configState = MutableStateFlow(parseJson(DefaultConfig.JSON_FALLBACK))
    val configState: StateFlow<AppConfig> = _configState.asStateFlow()

    init {
        // 1. Configure Firebase Remote Config
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // Fetch once an hour in production
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        // 2. Set the default offline values
        remoteConfig.setDefaultsAsync(mapOf(DefaultConfig.CONFIG_KEY to DefaultConfig.JSON_FALLBACK))

        // 3. Fetch the latest from the cloud
        fetchAndActivate()
    }

    /**
     * Force a fetch (useful for pull-to-refresh or returning from background)
     */
    fun fetchAndActivate() {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logger.d("RemoteConfigManager", "Config fetched and activated successfully.")
            } else {
                logger.e("RemoteConfigManager", "Fetch failed, using defaults or cached values.")
            }
            // Always update the flow, whether it's new cloud data or cached data
            updateStateFromFirebase()
        }
    }

    private fun updateStateFromFirebase() {
        val jsonString = remoteConfig.getString(DefaultConfig.CONFIG_KEY)
        _configState.value = parseJson(jsonString)
    }

    private fun parseJson(jsonStr: String): AppConfig {
        return try {
            json.decodeFromString<AppConfig>(jsonStr)
        } catch (e: Exception) {
            logger.e(
                "RemoteConfigManager",
                "Failed to parse JSON, falling back to safe defaults",
                e
            )
            AppConfig() // Absolute safety net: return standard defaults if JSON is corrupt
        }
    }
}
