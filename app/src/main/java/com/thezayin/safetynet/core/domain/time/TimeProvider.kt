package com.thezayin.safetynet.core.domain.time

interface TimeProvider {
    fun currentTimeMillis(): Long
    fun elapsedRealtime(): Long
}