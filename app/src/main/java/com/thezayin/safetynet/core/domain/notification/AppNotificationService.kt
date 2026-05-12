package com.thezayin.safetynet.core.domain.notification

interface AppNotificationService {
    fun initializeChannels()
    fun showTimerActiveNotification(remainingTimeText: String): Any
    fun showWarningNotification(): Any
    fun showCriticalNotification(): Any
    fun showZeroHourNotification(): Any
    fun showEmergencyDispatchedNotification(): Any
    fun cancelAll()
    fun cancelZeroHourNotification()
}