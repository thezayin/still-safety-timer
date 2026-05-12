package com.thezayin.safetynet.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val ONBOARDING_DS = "onboarding_datastore"
private const val PROFILE_DS = "profile_datastore"

val appModule = module {

    single(named(ONBOARDING_DS)) {
        PreferenceDataStoreFactory.create(
            produceFile = { androidContext().preferencesDataStoreFile("onboarding_prefs") }
        )
    }

    single(named(PROFILE_DS)) {
        PreferenceDataStoreFactory.create(
            produceFile = { androidContext().preferencesDataStoreFile("user_profile_secure") }
        )
    }
}