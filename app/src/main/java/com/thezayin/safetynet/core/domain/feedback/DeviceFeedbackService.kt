package com.thezayin.safetynet.core.domain.feedback

interface DeviceFeedbackService {
    fun triggerCheckInHaptic()
    fun triggerEmergencyVibration()
    fun stopEmergencyVibration()
}