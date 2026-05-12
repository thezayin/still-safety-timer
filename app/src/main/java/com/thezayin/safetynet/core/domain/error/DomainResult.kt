package com.thezayin.safetynet.core.domain.error

sealed class DomainResult<out T> {
    data class Success<out T>(val data: T) : DomainResult<T>()
    data class Failure(val error: AppError) : DomainResult<Nothing>()

    inline fun <R> fold(
        onSuccess: (T) -> R,
        onFailure: (AppError) -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Failure -> onFailure(error)
    }

    val isSuccess get() = this is Success
    val isFailure get() = this is Failure
}