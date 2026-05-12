package com.thezayin.safetynet.feature_timer.domain.model

data class TimerData(
    val isActive: Boolean,
    val startTimestamp: Long,
    val targetTimestamp: Long,
    val durationHours: Int,
    val lastCheckin: Long,
    val history: List<Long>,
    val streak: Int
)