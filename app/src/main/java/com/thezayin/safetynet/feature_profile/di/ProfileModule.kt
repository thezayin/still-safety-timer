package com.thezayin.safetynet.feature_profile.di

import com.thezayin.safetynet.feature_profile.data.repository.ProfileRepositoryImpl
import com.thezayin.safetynet.feature_profile.domain.repository.ProfileRepository
import com.thezayin.safetynet.feature_profile.domain.usecase.SaveProfileDataUseCase
import com.thezayin.safetynet.feature_profile.domain.usecase.ValidateProfileInputUseCase
import com.thezayin.safetynet.feature_profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    single<ProfileRepository> {
        ProfileRepositoryImpl(cryptoService = get())
    }
    factory {
        ValidateProfileInputUseCase()
    }

    factory {
        SaveProfileDataUseCase(profileRepository = get())
    }
    viewModelOf(::ProfileViewModel)
}