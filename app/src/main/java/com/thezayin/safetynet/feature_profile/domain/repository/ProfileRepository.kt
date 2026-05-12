package com.thezayin.safetynet.feature_profile.domain.repository

import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.feature_profile.domain.model.ProfileData
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    val profileData: Flow<ProfileData>

    suspend fun saveUserName(name: String): DomainResult<Unit>
    suspend fun saveContactName(name: String): DomainResult<Unit>
    suspend fun saveContactEmail(email: String): DomainResult<Unit>
    suspend fun clearAll(): DomainResult<Unit>
}