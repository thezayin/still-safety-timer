package com.thezayin.safetynet.core.domain.security

import com.thezayin.safetynet.core.domain.error.DomainResult

interface CryptoService {
    fun encryptAndSave(key: String, plainText: String): DomainResult<Unit>
    fun getAndDecrypt(key: String): DomainResult<String>
    fun delete(key: String): DomainResult<Unit>
    fun clearAll(): DomainResult<Unit>
}