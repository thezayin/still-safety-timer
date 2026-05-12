package com.thezayin.safetynet.feature_timer.presentation.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import com.thezayin.safetynet.core.domain.notification.AppNotificationService
import com.thezayin.safetynet.core.domain.utils.TimeFormatter
import com.thezayin.safetynet.core.infrastructure.notification.AndroidNotificationService
import com.thezayin.safetynet.feature_timer.data.repository.TimerHardwareManagerImpl
import com.thezayin.safetynet.feature_timer.domain.model.TimerPhase
import com.thezayin.safetynet.feature_timer.domain.usecase.ObserveTimerPhaseUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

class TimerService : Service() {

    private val notificationService: AppNotificationService by inject()
    private val observeTimerPhase: ObserveTimerPhaseUseCase by inject()
    private val logger: LocalLogger by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var observationJob: Job? = null

    companion object {
        private const val TAG = "TimerService"
        private const val NOTIFICATION_ID = AndroidNotificationService.NOTIFICATION_ID_ACTIVE
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            TimerHardwareManagerImpl.ACTION_START_SERVICE -> startForegroundSession()
            TimerHardwareManagerImpl.ACTION_STOP_SERVICE -> stopForegroundSession()
            null -> startForegroundSession()
        }
        return START_STICKY
    }

    private fun startForegroundSession() {
        if (observationJob != null && observationJob?.isActive == true) return

        logger.i(TAG, "Initiating foreground session...")

        val initialNotification =
            notificationService.showTimerActiveNotification("Initializing...") as Notification

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID,
                    initialNotification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(NOTIFICATION_ID, initialNotification)
            }
        } catch (e: Exception) {
            logger.e(TAG, "Critical: Failed to start foreground service", e)
            return
        }
        observationJob = observeTimerPhase().onEach { phase ->
            when (phase) {
                is TimerPhase.Active, is TimerPhase.Warning, is TimerPhase.Critical -> {
                    val remaining = when (phase) {
                        is TimerPhase.Active -> phase.remainingMillis
                        is TimerPhase.Warning -> phase.remainingMillis
                        is TimerPhase.Critical -> phase.remainingMillis
                    }
                    notificationService.showTimerActiveNotification(
                        TimeFormatter.formatCountdown(remaining)
                    )
                }

                is TimerPhase.Abort -> {
                    notificationService.showZeroHourNotification()
                }

                is TimerPhase.Idle, is TimerPhase.Expired -> {
                    logger.i(TAG, "Phase ended. Stopping service.")
                    stopForegroundSession()
                }
            }
        }.launchIn(serviceScope)
    }

    private fun stopForegroundSession() {
        logger.i(TAG, "Stopping foreground session...")
        observationJob?.cancel()
        observationJob = null

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        logger.i(TAG, "TimerService Destroyed.")
    }
}