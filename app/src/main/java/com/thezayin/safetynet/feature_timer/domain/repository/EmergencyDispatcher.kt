package com.thezayin.safetynet.feature_timer.domain.repository

import com.thezayin.safetynet.core.domain.error.DomainResult

interface EmergencyDispatcher {
    suspend fun sendEmailAlert(
        toEmail: String,
        toName: String,
        userName: String
    ): DomainResult<Unit>
}