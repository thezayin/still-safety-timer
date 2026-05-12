package com.thezayin.safetynet.feature_splash.di

import com.thezayin.safetynet.feature_splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val splashModule = module {
    viewModelOf(::SplashViewModel)
}