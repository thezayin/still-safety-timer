package com.thezayin.safetynet.core.domain.notification

interface AppNotificationService {
    fun initializeChannels()
    fun showTimerActiveNotification(remainingTimeText: String): Any
    fun showHalfTimeNotification(): Any
    fun showWarningNotification(): Any
    fun showCriticalNotification(): Any
    fun showImminentNotification(): Any
    fun showLastMinuteNotification(): Any
    fun showZeroHourAlertOnce(): Any
    fun showZeroHourUpdate(secondsRemaining: Int): Any
    fun showEmergencyDispatchedNotification(): Any
    fun cancelAll()
    fun cancelZeroHourNotification()
}