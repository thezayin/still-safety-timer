package com.thezayin.safetynet.core.domain.utils

import com.thezayin.safetynet.core.domain.error.AppError

sealed interface Result<out D> {
    data class Success<out D>(val data: D) : Result<D>
    data class Error(val error: AppError) : Result<Nothing>
}