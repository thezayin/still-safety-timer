package com.thezayin.safetynet.feature_timer.presentation.contract

import com.thezayin.safetynet.feature_timer.domain.model.TimerPhase

data class TimerState(
    val phase: TimerPhase = TimerPhase.Idle,
    val durationHours: Int = 48,
    val streakCount: Int = 0,
    val isLoading: Boolean = false,
    val isBatteryWarningActive: Boolean = false,
    val isBatterySaverAlertVisible: Boolean = false,
    val displayTime: String = "00:00:00"
)

sealed interface TimerIntent {
    data class StartTimer(val ignoreBatteryWarning: Boolean = false) : TimerIntent
    data object StopTimer : TimerIntent
    data object CheckIn : TimerIntent
    data object AbortEmergency : TimerIntent
    data class UpdateDuration(val hours: Int) : TimerIntent
    data object OnSettingsClicked : TimerIntent
}

sealed interface TimerEffect {
    data class ShowError(val message: String) : TimerEffect
    data object NavigateToSettings : TimerEffect
    data object TriggerSuccessHaptic : TimerEffect
    data object OpenExactAlarmSettings : TimerEffect
    data object TriggerBatteryExemptionPopup : TimerEffect
}