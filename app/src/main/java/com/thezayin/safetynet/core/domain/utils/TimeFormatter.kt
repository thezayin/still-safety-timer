package com.thezayin.safetynet.core.domain.utils

import com.thezayin.safetynet.feature_timer.domain.model.TimerPhase
import java.util.Locale
import java.util.concurrent.TimeUnit

object TimeFormatter {

    private const val MILLIS_PER_HOUR = 3_600_000L

    fun formatPhase(phase: TimerPhase): String {
        return when (phase) {
            is TimerPhase.Idle -> "00:00:00"
            is TimerPhase.Active -> formatCountdown(phase.remainingMillis)
            is TimerPhase.HalfTime -> formatCountdown(phase.remainingMillis)
            is TimerPhase.Warning -> formatCountdown(phase.remainingMillis)
            is TimerPhase.Critical -> formatCountdown(phase.remainingMillis)
            is TimerPhase.Imminent -> formatCountdown(phase.remainingMillis)
            is TimerPhase.LastMinute -> formatCountdown(phase.remainingMillis)
            is TimerPhase.Abort -> formatAbortSeconds(phase.secondsRemaining)
            is TimerPhase.Expired -> "00:00:00"
        }
    }

    fun formatCountdown(millis: Long): String {
        val absMillis = maxOf(0L, millis)
        val hours = TimeUnit.MILLISECONDS.toHours(absMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(absMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(absMillis) % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun calculateProgress(remainingMillis: Long, totalHours: Int): Float {
        val totalMillis = totalHours * MILLIS_PER_HOUR
        if (totalMillis <= 0L) return 0f
        return (remainingMillis.toFloat() / totalMillis.toFloat()).coerceIn(0f, 1f)
    }

    private fun formatAbortSeconds(seconds: Int): String =
        String.format(Locale.getDefault(), "%02d", maxOf(0, seconds))
}