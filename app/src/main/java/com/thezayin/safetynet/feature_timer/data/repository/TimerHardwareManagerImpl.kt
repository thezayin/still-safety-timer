package com.thezayin.safetynet.feature_timer.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import com.thezayin.safetynet.feature_timer.data.worker.EmergencyAlertWorker
import com.thezayin.safetynet.feature_timer.domain.repository.TimerHardwareManager
import com.thezayin.safetynet.feature_timer.presentation.service.TimerService
import java.util.concurrent.TimeUnit

class TimerHardwareManagerImpl(
    context: Context,
    private val logger: LocalLogger
) : TimerHardwareManager {

    private val appContext = context.applicationContext
    private val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val workManager = WorkManager.getInstance(appContext)

    companion object {
        private const val TAG = "TimerHardwareManager"
        const val ACTION_ZERO_HOUR = "com.thezayin.safetynet.ACTION_ZERO_HOUR"
        const val ACTION_START_SERVICE = "com.thezayin.safetynet.ACTION_START_SERVICE"
        const val ACTION_STOP_SERVICE = "com.thezayin.safetynet.ACTION_STOP_SERVICE"
        private const val EMERGENCY_WORK_NAME = "unique_emergency_alert_dispatch"
        private const val ALARM_REQUEST_CODE = 999
    }

    // In TimerHardwareManagerImpl.kt
    override fun startSafetyService(targetTimestamp: Long): DomainResult<Unit> {
        return try {
            val intent = Intent(appContext, TimerService::class.java)
            // Add this line to force the OS to accept it as a foreground request
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appContext.startForegroundService(intent)
            } else {
                appContext.startService(intent)
            }
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            logger.e(TAG, "Failed to start service: ${e.message}")
            DomainResult.Failure(AppError.Timer.SchedulingFailed(e))
        }
    }

    override fun stopSafetyService(): DomainResult<Unit> {
        val intent = Intent(ACTION_STOP_SERVICE).apply {
            setPackage(appContext.packageName)
        }
        appContext.stopService(intent)
        logger.i(TAG, "Safety Service stop commanded.")

        return DomainResult.Success(Unit)
    }

    override fun scheduleExactAlarms(targetTimestamp: Long): DomainResult<Unit> {
        val pendingIntent = getZeroHourPendingIntent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            logger.e(TAG, "Exact alarm permission missing.")
            return DomainResult.Failure(AppError.Permission.ExactAlarmDenied)
        }

        return try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                targetTimestamp,
                pendingIntent
            )
            logger.i(TAG, "Exact Alarm scheduled for $targetTimestamp")
            DomainResult.Success(Unit)
        } catch (_: SecurityException) {
            DomainResult.Failure(AppError.Permission.ExactAlarmDenied)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.Timer.SchedulingFailed(e))
        }
    }

    override fun cancelExactAlarms(): DomainResult<Unit> {
        alarmManager.cancel(getZeroHourPendingIntent())
        return DomainResult.Success(Unit)
    }

    override fun enqueueEmergencyWorker(): DomainResult<Unit> {
        val constraints = Constraints.Builder()
            .build()

        val alertRequest = OneTimeWorkRequestBuilder<EmergencyAlertWorker>()
            .setInitialDelay(60, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniqueWork(EMERGENCY_WORK_NAME, ExistingWorkPolicy.REPLACE, alertRequest)
        return DomainResult.Success(Unit)
    }

    override fun cancelEmergencyWorker(): DomainResult<Unit> {
        workManager.cancelUniqueWork(EMERGENCY_WORK_NAME)
        return DomainResult.Success(Unit)
    }

    private fun getZeroHourPendingIntent(): PendingIntent {
        val intent = Intent(appContext, com.thezayin.safetynet.feature_timer.presentation.receiver.TimerAlarmReceiver::class.java).apply {
            action = ACTION_ZERO_HOUR
        }

        return PendingIntent.getBroadcast(
            appContext,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}