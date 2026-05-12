@file:Suppress("DEPRECATION")

package com.thezayin.safetynet.core.infrastructure.security

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import com.thezayin.safetynet.core.domain.security.CryptoService

class AndroidCryptoService(
    context: Context, private val logger: LocalLogger
) : CryptoService {

    private val appContext = context.applicationContext

    companion object {
        private const val VAULT_FILE_NAME = "still_secure_vault"
        private const val TAG = "AndroidCryptoService"
    }

    private val masterKey by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        MasterKey.Builder(appContext).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    }

    private val securePrefs by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        EncryptedSharedPreferences.create(
            appContext,
            VAULT_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun encryptAndSave(key: String, plainText: String): DomainResult<Unit> {
        return try {
            securePrefs.edit { putString(key, plainText) }
            logger.d(TAG, "Encrypted and saved key=$key")
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            logger.e(TAG, "Failed to encrypt key=$key", e)
            DomainResult.Failure(AppError.System.EncryptionFailed)
        }
    }

    override fun getAndDecrypt(key: String): DomainResult<String> {
        return try {
            val decrypted = securePrefs.getString(key, null)
            if (decrypted != null) {
                logger.d(TAG, "Decrypted key=$key successfully")
                DomainResult.Success(decrypted)
            } else {
                logger.w(TAG, "Key=$key not found in vault")
                DomainResult.Failure(AppError.System.DecryptionFailed)
            }
        } catch (e: Exception) {
            logger.e(TAG, "Failed to decrypt key=$key", e)
            DomainResult.Failure(AppError.System.DecryptionFailed)
        }
    }

    override fun delete(key: String): DomainResult<Unit> {
        return try {
            securePrefs.edit { remove(key) }
            logger.d(TAG, "Deleted key=$key from vault")
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            logger.e(TAG, "Failed to delete key=$key", e)
            DomainResult.Failure(AppError.System.DatabaseError)
        }
    }

    override fun clearAll(): DomainResult<Unit> {
        return try {
            securePrefs.edit { clear() }
            logger.i(TAG, "Vault cleared — all encrypted data removed")
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            logger.e(TAG, "Failed to clear vault", e)
            DomainResult.Failure(AppError.System.DatabaseError)
        }
    }
}