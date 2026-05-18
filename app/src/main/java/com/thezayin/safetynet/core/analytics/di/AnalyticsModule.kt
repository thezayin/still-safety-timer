package com.thezayin.safetynet.core.analytics.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.thezayin.safetynet.BuildConfig
import com.thezayin.safetynet.core.analytics.AnalyticsManager
import com.thezayin.safetynet.core.analytics.tracker.AnalyticsTracker
import com.thezayin.safetynet.core.analytics.tracker.DebugAnalyticsTracker
import com.thezayin.safetynet.core.analytics.tracker.FirebaseAnalyticsTracker
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val analyticsModule = module {

    /**
     * Firebase instances.
     */
    single { FirebaseAnalytics.getInstance(androidContext()) }
    single { FirebaseCrashlytics.getInstance() }

    /**
     * Firebase tracker — always present in every build.
     */
    single<FirebaseAnalyticsTracker> {
        FirebaseAnalyticsTracker(
            firebaseAnalytics = get(),
            crashlytics = get(),
            logger = get()
        )
    }

    /**
     * AnalyticsManager — singleton for the entire app process.
     * Inject this into ViewModels, Workers, and AdManager via Koin.
     * * We build the tracker list dynamically here so we don't pollute the
     * Koin graph with nullable or unused dependencies in release builds.
     */
    single {
        val trackers = mutableListOf<AnalyticsTracker>(get<FirebaseAnalyticsTracker>())

        // Add the debug tracker ONLY if it's a debug build
        if (BuildConfig.DEBUG) {
            trackers.add(DebugAnalyticsTracker())
        }

        AnalyticsManager(trackers)
    }
}