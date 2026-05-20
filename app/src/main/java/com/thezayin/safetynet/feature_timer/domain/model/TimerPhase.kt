package com.thezayin.safetynet.feature_timer.domain.model

sealed class TimerPhase {
    data object Idle : TimerPhase()
    data class Active(val remainingMillis: Long) : TimerPhase()
    data class HalfTime(val remainingMillis: Long) : TimerPhase()   // NEW
    data class Warning(val remainingMillis: Long) : TimerPhase()    // 6hr
    data class Critical(val remainingMillis: Long) : TimerPhase()   // 1hr
    data class Imminent(val remainingMillis: Long) : TimerPhase()   // 30min NEW
    data class LastMinute(val remainingMillis: Long) : TimerPhase() // 1min NEW
    data class Abort(val secondsRemaining: Int) : TimerPhase()
    data class Expired(val elapsedMillis: Long) : TimerPhase()
}