package com.thezayin.safetynet.core.remoteconfig

object DefaultConfig {
    const val CONFIG_KEY = "app_config_json"

    // This perfectly matches your default data class values
    const val JSON_FALLBACK = """
        {
          "ads": {
            "master_kill_switch": true,
            "placements": {
              "splash_app_open": true,
              "home_native": true,
              "result_rewarded": true,
              "settings_banner": false
            },
            "max_retries": 3,
            "retry_base_delay_ms": 2000,
            "splash_timeout_ms": 3000
          },
          "safety_mechanics": {
            "is_email_dispatch_enabled": true,
            "max_timer_duration_hours": 72,
            "emergency_contact_limit_free": 3
          },
          "lifecycle": {
            "min_required_version_code": 1,
            "force_update_message": "Please update the app to continue."
          }
        }
    """
}