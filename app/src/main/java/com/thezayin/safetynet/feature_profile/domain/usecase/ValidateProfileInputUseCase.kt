package com.thezayin.safetynet.feature_profile.domain.usecase

import com.thezayin.safetynet.core.domain.error.AppError

class ValidateProfileInputUseCase {

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

    fun validateUserName(input: String): AppError.Profile? {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return AppError.Profile.UserNameMissing
        if (trimmed.length < 2 || !trimmed.any { it.isLetter() }) {
            return AppError.Profile.ValidationFailed("userName", "Must be 2+ characters with letters")
        }
        return null
    }

    fun validateContactName(input: String): AppError.Profile? {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return AppError.Profile.ContactNameMissing
        if (trimmed.length < 2 || !trimmed.any { it.isLetter() }) {
            return AppError.Profile.ValidationFailed("contactName", "Must be 2+ characters with letters")
        }
        return null
    }

    fun validateEmail(input: String): AppError.Profile? {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return AppError.Profile.ContactEmailMissing
        if (!emailRegex.matches(trimmed)) {
            return AppError.Profile.ValidationFailed("email", "Invalid email format")
        }
        return null
    }
}