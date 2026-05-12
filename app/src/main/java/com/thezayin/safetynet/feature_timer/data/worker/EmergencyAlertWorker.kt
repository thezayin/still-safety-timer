package com.thezayin.safetynet.feature_timer.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import com.thezayin.safetynet.feature_timer.domain.usecase.SendEmergencyEmailUseCase
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EmergencyAlertWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val sendEmailUseCase: SendEmergencyEmailUseCase by inject()
    private val repository: TimerRepository by inject()
    private val logger: LocalLogger by inject()

    companion object {
        private const val TAG = "EmergencyAlertWorker"
    }

    override suspend fun doWork(): Result {
        val timerData = repository.timerData.first()
        if (!timerData.isActive) {
            logger.i(TAG, "Safety check: Timer inactive, aborting SOS.")
            return Result.success()
        }

        logger.i(TAG, "Dispatching SOS protocol...")
        return when (val result = sendEmailUseCase()) {
            is DomainResult.Success -> {
                logger.i(TAG, "SOS dispatched.")
                Result.success()
            }

            is DomainResult.Failure -> {
                when (result.error) {
                    is AppError.Alert.NetworkFailure, is AppError.Alert.SendFailed -> {
                        logger.w(TAG, "Network error, retrying...")
                        Result.retry()
                    }

                    else -> {
                        logger.e(TAG, "Critical failure: ${result.error}")
                        Result.failure()
                    }
                }
            }
        }
    }
}