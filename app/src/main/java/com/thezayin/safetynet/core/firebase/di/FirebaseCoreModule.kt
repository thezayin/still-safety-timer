package com.thezayin.safetynet.core.firebase.di

//import com.google.firebase.appcheck.FirebaseAppCheck
//import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import org.koin.dsl.module

val firebaseCoreModule = module {
    // Crashlytics Singleton
    single { FirebaseCrashlytics.getInstance() }

    // Performance Monitoring Singleton
    single { FirebasePerformance.getInstance() }
    // App Check Initialization
//    single {
//        val firebaseAppCheck = FirebaseAppCheck.getInstance()
//        firebaseAppCheck.installAppCheckProviderFactory(
//            PlayIntegrityAppCheckProviderFactory.getInstance()
//        )
//        firebaseAppCheck
//    }
}