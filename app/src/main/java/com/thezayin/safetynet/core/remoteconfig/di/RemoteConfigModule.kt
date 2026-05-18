package com.thezayin.safetynet.core.remoteconfig.di

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.thezayin.safetynet.core.remoteconfig.RemoteConfigManager
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val remoteConfigModule = module {

    // Provide the Firebase instance
    single { FirebaseRemoteConfig.getInstance() }

    // Provide Kotlinx Serialization Json configured for Remote Config
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
        }
    }

    // Provide the Manager as a Singleton
    single {
        RemoteConfigManager(
            remoteConfig = get(),
            json = get(),
            logger = get()
        )
    }
}
