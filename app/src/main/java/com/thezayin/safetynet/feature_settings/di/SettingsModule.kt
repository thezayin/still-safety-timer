package com.thezayin.safetynet.feature_settings.di

import com.thezayin.safetynet.feature_settings.data.repository.SettingsRepositoryImpl
import com.thezayin.safetynet.feature_settings.data.source.SettingsDataSource
import com.thezayin.safetynet.feature_settings.domain.repository.SettingsRepository
import com.thezayin.safetynet.feature_settings.domain.usecase.*
import com.thezayin.safetynet.feature_settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {
    single { SettingsDataSource() }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    factory { ValidateSettingsUseCase() }
    factory { UpdateIntervalUseCase(get()) }
    factory { GetSupportEmailUseCase(get()) }
    factory {
        WipeAllDataUseCase(
            timerRepository = get(),
            profileRepository = get(),
            onboardingRepository = get(),
            hardwareManager = get()
        )
    }

    viewModelOf(::SettingsViewModel)
}