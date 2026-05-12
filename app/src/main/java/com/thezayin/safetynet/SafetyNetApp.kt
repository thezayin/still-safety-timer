package com.thezayin.safetynet

import android.app.Application
import com.thezayin.safetynet.core.di.coreModule
import com.thezayin.safetynet.di.appModule
import com.thezayin.safetynet.feature_onboarding.di.onboardingModule
import com.thezayin.safetynet.feature_profile.di.profileModule
import com.thezayin.safetynet.feature_settings.di.settingsModule
import com.thezayin.safetynet.feature_splash.di.splashModule
import com.thezayin.safetynet.feature_timer.di.timerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class SafetyNetApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.ERROR)
            androidContext(this@SafetyNetApp)
            workManagerFactory()
            modules(
                appModule,
                coreModule,
                profileModule,
                onboardingModule,
                splashModule,
                timerModule,
                settingsModule
            )
        }
    }
}