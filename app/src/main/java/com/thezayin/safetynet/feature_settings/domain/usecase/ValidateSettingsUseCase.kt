package com.thezayin.safetynet.feature_settings.domain.usecase

import com.thezayin.safetynet.core.domain.error.AppError

class ValidateSettingsUseCase {

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

    fun validateName(input: String): AppError.Profile? {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return AppError.Profile.UserNameMissing
        if (trimmed.length < 2 || !trimmed.any { it.isLetter() }) {
            return AppError.Profile.ValidationFailed("name", "Must be 2+ characters")
        }
        return null
    }

    fun validateEmail(input: String): AppError.Profile? {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return AppError.Profile.ContactEmailMissing
        if (!emailRegex.matches(trimmed)) {
            return AppError.Profile.ValidationFailed("email", "Invalid format")
        }
        return null
    }
}