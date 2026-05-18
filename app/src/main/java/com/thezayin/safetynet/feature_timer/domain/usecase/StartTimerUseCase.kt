package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.monitor.PowerMonitor
import com.thezayin.safetynet.core.domain.time.TimeProvider
import com.thezayin.safetynet.feature_timer.domain.repository.TimerHardwareManager
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import kotlinx.coroutines.flow.first

class StartTimerUseCase(
    private val repository: TimerRepository,
    private val hardwareManager: TimerHardwareManager,
    private val timeProvider: TimeProvider,
    private val powerMonitor: PowerMonitor
) {
    suspend operator fun invoke(
        overrideHours: Int? = null,
        bypassBatteryCheck: Boolean = false
    ): DomainResult<Unit> {

        if (powerMonitor.isBatterySaverEnabled()) {
            return DomainResult.Failure(AppError.System.PowerSaverActive)
        }
        if (!bypassBatteryCheck && !powerMonitor.isIgnoringBatteryOptimizations()) {
            return DomainResult.Failure(AppError.System.PowerSaverActive)
        }

        val data = repository.timerData.first()
        if (data.isActive) return DomainResult.Failure(AppError.Timer.AlreadyActive)

        val durationHours = overrideHours ?: data.durationHours
        val start = timeProvider.currentTimeMillis()
//        val target = start + (durationHours * 3600000L)
        val target = start + (durationHours * 1500L)
        val alarmResult = hardwareManager.scheduleExactAlarms(target)
        if (alarmResult is DomainResult.Failure) return alarmResult

        val dbResult = repository.setTimerActive(isActive = true, start = start, target = target)
        if (dbResult is DomainResult.Failure) {
            hardwareManager.cancelExactAlarms()
            return dbResult
        }

        val serviceResult = hardwareManager.startSafetyService(target)
        if (serviceResult is DomainResult.Failure) {
            repository.deactivateAndClear()
            hardwareManager.cancelExactAlarms()
            return serviceResult
        }

        return DomainResult.Success(Unit)
    }
}