package com.thezayin.safetynet.feature_profile.data.repository

import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.security.CryptoService
import com.thezayin.safetynet.feature_profile.domain.model.ProfileData
import com.thezayin.safetynet.feature_profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileRepositoryImpl(
    private val cryptoService: CryptoService
) : ProfileRepository {

    companion object {
        private const val KEY_USER_NAME = "pii_user_name"
        private const val KEY_CONTACT_NAME = "pii_contact_name"
        private const val KEY_CONTACT_EMAIL = "pii_contact_email"
    }

    private val _profileData = MutableStateFlow(loadDecryptedData())
    override val profileData: Flow<ProfileData> = _profileData.asStateFlow()
    private fun loadDecryptedData(): ProfileData {
        val uName = cryptoService.getAndDecrypt(KEY_USER_NAME).fold(
            onSuccess = { it },
            onFailure = { "" }
        )
        val cName = cryptoService.getAndDecrypt(KEY_CONTACT_NAME).fold(
            onSuccess = { it },
            onFailure = { "" }
        )
        val cEmail = cryptoService.getAndDecrypt(KEY_CONTACT_EMAIL).fold(
            onSuccess = { it },
            onFailure = { "" }
        )
        return ProfileData(uName, cName, cEmail)
    }

    override suspend fun saveUserName(name: String): DomainResult<Unit> {
        return cryptoService.encryptAndSave(KEY_USER_NAME, name).also { result ->
            if (result.isSuccess) {
                _profileData.update { it.copy(userName = name) }
            }
        }
    }

    override suspend fun saveContactName(name: String): DomainResult<Unit> {
        return cryptoService.encryptAndSave(KEY_CONTACT_NAME, name).also { result ->
            if (result.isSuccess) {
                _profileData.update { it.copy(contactName = name) }
            }
        }
    }

    override suspend fun saveContactEmail(email: String): DomainResult<Unit> {
        return cryptoService.encryptAndSave(KEY_CONTACT_EMAIL, email).also { result ->
            if (result.isSuccess) {
                _profileData.update { it.copy(contactEmail = email) }
            }
        }
    }

    override suspend fun clearAll(): DomainResult<Unit> {
        cryptoService.delete(KEY_USER_NAME)
        cryptoService.delete(KEY_CONTACT_NAME)
        cryptoService.delete(KEY_CONTACT_EMAIL)
        _profileData.update { ProfileData() }
        return DomainResult.Success(Unit)
    }
}