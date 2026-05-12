package com.thezayin.safetynet.feature_timer.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.thezayin.safetynet.core.domain.monitor.PowerMonitor
import com.thezayin.safetynet.core.domain.notification.AppNotificationService
import com.thezayin.safetynet.core.infrastructure.monitor.AndroidPowerMonitor
import com.thezayin.safetynet.core.infrastructure.notification.AndroidNotificationService
import com.thezayin.safetynet.feature_timer.data.remote.MailjetClient
import com.thezayin.safetynet.feature_timer.data.repository.EmergencyDispatcherImpl
import com.thezayin.safetynet.feature_timer.data.repository.TimerHardwareManagerImpl
import com.thezayin.safetynet.feature_timer.data.repository.TimerRepositoryImpl
import com.thezayin.safetynet.feature_timer.data.worker.EmergencyAlertWorker
import com.thezayin.safetynet.feature_timer.domain.repository.EmergencyDispatcher
import com.thezayin.safetynet.feature_timer.domain.repository.TimerHardwareManager
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import com.thezayin.safetynet.feature_timer.domain.usecase.AbortEmergencyUseCase
import com.thezayin.safetynet.feature_timer.domain.usecase.CalculateStreakUseCase
import com.thezayin.safetynet.feature_timer.domain.usecase.CheckInUseCase
import com.thezayin.safetynet.feature_timer.domain.usecase.ObserveTimerPhaseUseCase
import com.thezayin.safetynet.feature_timer.domain.usecase.RestoreTimerUseCase
import com.thezayin.safetynet.feature_timer.domain.usecase.SendEmergencyEmailUseCase
import com.thezayin.safetynet.feature_timer.domain.usecase.StartTimerUseCase
import com.thezayin.safetynet.feature_timer.domain.usecase.StopTimerUseCase
import com.thezayin.safetynet.feature_timer.domain.usecase.TriggerZeroHourUseCase
import com.thezayin.safetynet.feature_timer.domain.usecase.UpdateTimerDurationUseCase
import com.thezayin.safetynet.feature_timer.presentation.TimerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val timerModule = module {
    single { MailjetClient.create(get()) }

    single {
        PreferenceDataStoreFactory.create(
            produceFile = { androidContext().preferencesDataStoreFile("safety_timer_prefs") })
    }

    single<TimerRepository> { TimerRepositoryImpl(get(), get()) }

    single<EmergencyDispatcher> { EmergencyDispatcherImpl(get()) }

    single<TimerHardwareManager> { TimerHardwareManagerImpl(androidContext(), get()) }

    single<AppNotificationService> { AndroidNotificationService(androidContext(), get()) }

    single<PowerMonitor> { AndroidPowerMonitor(androidContext()) }

    factory { StartTimerUseCase(get(), get(), get(), get()) }
    factory { StopTimerUseCase(get(), get(), get(), get()) }
    factory { CheckInUseCase(get(), get(), get(), get(), get()) }
    factory { AbortEmergencyUseCase(get(), get(), get(), get()) }
    factory { UpdateTimerDurationUseCase(get()) }
    factory { ObserveTimerPhaseUseCase(get(), get()) }
    factory { CalculateStreakUseCase(get(), get()) }
    factory { RestoreTimerUseCase(get(), get(), get()) }
    factory {
        TriggerZeroHourUseCase(
            get(), get(), notificationService = get()
        )
    }
    factory { SendEmergencyEmailUseCase(get(), get(), get()) }

    viewModelOf(::TimerViewModel)

    worker { EmergencyAlertWorker(get(), get()) }
}