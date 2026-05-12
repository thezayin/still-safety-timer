package com.thezayin.safetynet.feature_onboarding.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.thezayin.safetynet.feature_onboarding.data.permission.PermissionCheckerImpl
import com.thezayin.safetynet.feature_onboarding.data.repository.OnboardingRepositoryImpl
import com.thezayin.safetynet.feature_onboarding.domain.permission.PermissionChecker
import com.thezayin.safetynet.feature_onboarding.domain.repository.OnboardingRepository
import com.thezayin.safetynet.feature_onboarding.domain.usecase.CompleteSlidesUseCase
import com.thezayin.safetynet.feature_onboarding.domain.usecase.SubmitConsentUseCase
import com.thezayin.safetynet.feature_onboarding.presentation.consent.ConsentViewModel
import com.thezayin.safetynet.feature_onboarding.presentation.permissions.PermissionViewModel
import com.thezayin.safetynet.feature_onboarding.presentation.slides.SlideViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val Context.onboardingDataStore by preferencesDataStore(name = "safetynet_onboarding_prefs")

val onboardingModule = module {

    single { androidContext().onboardingDataStore }

    single<OnboardingRepository> {
        OnboardingRepositoryImpl(dataStore = get())
    }

    single<PermissionChecker> {
        PermissionCheckerImpl(context = androidContext())
    }

    factory { CompleteSlidesUseCase(repository = get()) }
    factory { SubmitConsentUseCase(repository = get()) }
    viewModelOf(::SlideViewModel)
    viewModelOf(::ConsentViewModel)
    viewModelOf(::PermissionViewModel)
}