package com.thezayin.safetynet.core.domain.error

sealed interface AppError {

    sealed interface Permission : AppError {
        data object ExactAlarmDenied : Permission
    }

    sealed interface Profile : AppError {
        data object UserNameMissing : Profile
        data object ContactNameMissing : Profile
        data object ContactEmailMissing : Profile
        data class ValidationFailed(
            val field: String, val reason: String
        ) : Profile
    }

    sealed interface Timer : AppError {
        data object AlreadyActive : Timer
        data object NotActive : Timer
        data class SchedulingFailed(val cause: Throwable) : Timer
        data class PersistenceFailed(val cause: Throwable) : Timer
    }

    sealed interface Alert : AppError {
        data object ProfileDataMissing : Alert
        data class NetworkFailure(
            val code: Int, val body: String
        ) : Alert

        data class SendFailed(val cause: Throwable) : Alert
    }

    sealed interface System : AppError {
        data object EncryptionFailed : System
        data object DecryptionFailed : System
        data object DatabaseError : System
        data object PowerSaverActive : System
    }
}