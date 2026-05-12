package com.thezayin.safetynet.feature_onboarding.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.feature_onboarding.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

@Suppress("LocalVariableName")
class OnboardingRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : OnboardingRepository {

    private companion object {
        val SLIDES_COMPLETED_KEY = booleanPreferencesKey("slides_completed")
        val CONSENT_ACCEPTED_KEY = booleanPreferencesKey("consent_accepted")
        val PROFILE_COMPLETED_KEY = booleanPreferencesKey("profile_completed")
    }

    override val isSlidesCompleted: Flow<Boolean> =
        dataStore.data.catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { it[SLIDES_COMPLETED_KEY] ?: false }

    override val isConsentAccepted: Flow<Boolean> =
        dataStore.data.catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { it[CONSENT_ACCEPTED_KEY] ?: false }

    override val isProfileComplete: Flow<Boolean> =
        dataStore.data.catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { it[PROFILE_COMPLETED_KEY] ?: false }

    override suspend fun setSlidesCompleted(): DomainResult<Unit> = safeEdit {
        it[SLIDES_COMPLETED_KEY] = true
    }

    override suspend fun setConsentAccepted(): DomainResult<Unit> = safeEdit {
        it[CONSENT_ACCEPTED_KEY] = true
    }

    override suspend fun setProfileComplete(): DomainResult<Unit> = safeEdit {
        it[PROFILE_COMPLETED_KEY] = true
    }

    private suspend fun safeEdit(action: (MutablePreferences: MutablePreferences) -> Unit): DomainResult<Unit> {
        return try {
            dataStore.edit { action(it) }
            DomainResult.Success(Unit)
        } catch (_: Exception) {
            DomainResult.Failure(AppError.System.DatabaseError)
        }
    }
}