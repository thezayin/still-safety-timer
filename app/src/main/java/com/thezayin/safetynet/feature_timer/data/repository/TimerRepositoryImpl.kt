package com.thezayin.safetynet.feature_timer.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import com.thezayin.safetynet.feature_timer.domain.model.TimerData
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class TimerRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val logger: LocalLogger
) : TimerRepository {

    companion object {
        private const val TAG = "TimerRepositoryImpl"
        private val KEY_IS_ACTIVE = booleanPreferencesKey("timer_is_active")
        private val KEY_START_TIMESTAMP = longPreferencesKey("timer_start_timestamp")
        private val KEY_TARGET_TIMESTAMP = longPreferencesKey("timer_target_timestamp")
        private val KEY_DURATION_HOURS = intPreferencesKey("timer_duration_hours")
        private val KEY_LAST_CHECKIN = longPreferencesKey("timer_last_checkin")
        private val KEY_HISTORY = stringPreferencesKey("timer_history_csv")
        private val KEY_STREAK = intPreferencesKey("timer_streak")
    }

    override val timerData: Flow<TimerData> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                logger.e(TAG, "Error reading DataStore", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            TimerData(
                isActive = preferences[KEY_IS_ACTIVE] ?: false,
                startTimestamp = preferences[KEY_START_TIMESTAMP] ?: 0L,
                targetTimestamp = preferences[KEY_TARGET_TIMESTAMP] ?: 0L,
                durationHours = preferences[KEY_DURATION_HOURS] ?: 48,
                lastCheckin = preferences[KEY_LAST_CHECKIN] ?: 0L,
                history = parseHistory(preferences[KEY_HISTORY]),
                streak = preferences[KEY_STREAK] ?: 0
            )
        }

    override suspend fun setTimerActive(
        isActive: Boolean,
        start: Long,
        target: Long
    ): DomainResult<Unit> {
        return try {
            dataStore.edit { prefs ->
                prefs[KEY_IS_ACTIVE] = isActive
                prefs[KEY_START_TIMESTAMP] = start
                prefs[KEY_TARGET_TIMESTAMP] = target
            }
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.Timer.PersistenceFailed(e))
        }
    }

    override suspend fun updateDuration(hours: Int): DomainResult<Unit> {
        return try {
            dataStore.edit { prefs ->
                prefs[KEY_DURATION_HOURS] = hours
            }
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.Timer.PersistenceFailed(e))
        }
    }

    override suspend fun recordCheckIn(timestamp: Long, history: List<Long>): DomainResult<Unit> {
        return try {
            dataStore.edit { prefs ->
                prefs[KEY_LAST_CHECKIN] = timestamp
                prefs[KEY_HISTORY] = history.joinToString(",")
            }
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.Timer.PersistenceFailed(e))
        }
    }

    override suspend fun deactivateAndClear(): DomainResult<Unit> {
        return try {
            dataStore.edit { prefs ->
                prefs[KEY_IS_ACTIVE] = false
                prefs[KEY_START_TIMESTAMP] = 0L
                prefs[KEY_TARGET_TIMESTAMP] = 0L
            }
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.Timer.PersistenceFailed(e))
        }
    }

    private fun parseHistory(csv: String?): List<Long> {
        if (csv.isNullOrBlank()) return emptyList()
        return try {
            csv.split(",").map { it.toLong() }
        } catch (_: NumberFormatException) {
            emptyList()
        }
    }
}