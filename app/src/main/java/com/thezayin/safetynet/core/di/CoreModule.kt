package com.thezayin.safetynet.core.di

import com.thezayin.safetynet.core.domain.feedback.DeviceFeedbackService
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import com.thezayin.safetynet.core.domain.monitor.PowerMonitor
import com.thezayin.safetynet.core.domain.notification.AppNotificationService
import com.thezayin.safetynet.core.domain.security.CryptoService
import com.thezayin.safetynet.core.domain.time.TimeProvider
import com.thezayin.safetynet.core.infrastructure.feedback.AndroidDeviceFeedback
import com.thezayin.safetynet.core.infrastructure.logger.AndroidLocalLogger
import com.thezayin.safetynet.core.infrastructure.monitor.AndroidPowerMonitor
import com.thezayin.safetynet.core.infrastructure.notification.AndroidNotificationService
import com.thezayin.safetynet.core.infrastructure.security.AndroidCryptoService
import com.thezayin.safetynet.core.infrastructure.time.AndroidTimeProvider
import com.thezayin.safetynet.feature_onboarding.data.permission.PermissionCheckerImpl
import com.thezayin.safetynet.feature_onboarding.domain.permission.PermissionChecker // ADDED IMPORT
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {

    single<LocalLogger> {
        AndroidLocalLogger(get())
    }

    single<TimeProvider> {
        AndroidTimeProvider()
    }

    single<PowerMonitor> {
        AndroidPowerMonitor(androidContext())
    }

    single<CryptoService> {
        AndroidCryptoService(
            context = androidContext(), logger = get()
        )
    }

    single<DeviceFeedbackService> {
        AndroidDeviceFeedback(
            androidContext(), logger = get()
        )
    }

    single<AppNotificationService> {
        AndroidNotificationService(
            context = androidContext(), logger = get()
        )
    }

    single<PermissionChecker> {
        PermissionCheckerImpl(context = androidContext())
    }
}