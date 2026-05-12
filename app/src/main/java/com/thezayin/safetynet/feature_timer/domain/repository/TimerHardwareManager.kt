package com.thezayin.safetynet.feature_timer.domain.repository

import com.thezayin.safetynet.core.domain.error.DomainResult

interface TimerHardwareManager {
    fun startSafetyService(targetTimestamp: Long): DomainResult<Unit>
    fun stopSafetyService(): DomainResult<Unit>
    fun scheduleExactAlarms(targetTimestamp: Long): DomainResult<Unit>
    fun cancelExactAlarms(): DomainResult<Unit>
    fun enqueueEmergencyWorker(): DomainResult<Unit>
    fun cancelEmergencyWorker(): DomainResult<Unit>
}