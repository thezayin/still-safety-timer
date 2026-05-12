package com.thezayin.safetynet.feature_timer.domain.repository

import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.feature_timer.domain.model.TimerData
import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    val timerData: Flow<TimerData>
    suspend fun setTimerActive(
        isActive: Boolean,
        start: Long,
        target: Long
    ): DomainResult<Unit>

    suspend fun updateDuration(hours: Int): DomainResult<Unit>
    suspend fun recordCheckIn(
        timestamp: Long,
        history: List<Long>
    ): DomainResult<Unit>

    suspend fun deactivateAndClear(): DomainResult<Unit>
}