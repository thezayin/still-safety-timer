package com.thezayin.safetynet.core.domain.monitor

import kotlinx.coroutines.flow.Flow

interface PowerMonitor {
    fun isBatterySaverEnabled(): Boolean
    fun isIgnoringBatteryOptimizations(): Boolean
    val batterySaverState: Flow<Boolean>
}