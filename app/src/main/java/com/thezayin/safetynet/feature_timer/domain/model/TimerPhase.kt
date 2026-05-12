package com.thezayin.safetynet.feature_timer.domain.model

sealed class TimerPhase {
    data object Idle : TimerPhase()
    data class Active(val remainingMillis: Long) : TimerPhase()
    data class Warning(val remainingMillis: Long) : TimerPhase()
    data class Critical(val remainingMillis: Long) : TimerPhase()
    data class Abort(val abortSecondsRemaining: Int) : TimerPhase()
    data class Expired(val overdueMillis: Long) : TimerPhase()
}