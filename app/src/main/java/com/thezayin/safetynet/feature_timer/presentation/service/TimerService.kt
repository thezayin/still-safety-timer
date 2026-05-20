package com.thezayin.safetynet.feature_timer.presentation.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.thezayin.safetynet.core.domain.feedback.DeviceFeedbackService
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import com.thezayin.safetynet.core.domain.notification.AppNotificationService
import com.thezayin.safetynet.core.domain.utils.TimeFormatter
import com.thezayin.safetynet.feature_timer.domain.model.TimerPhase
import com.thezayin.safetynet.feature_timer.domain.usecase.ObserveTimerPhaseUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

class TimerService : Service() {

    private val notificationService: AppNotificationService by inject()
    private val observeTimerPhase: ObserveTimerPhaseUseCase by inject()
    private val logger: LocalLogger by inject()

    private val feedbackService: DeviceFeedbackService by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var observationJob: Job? = null
    private var lastNotifiedPhase: TimerPhase? = null

    companion object {
        private const val TAG = "TimerService"
    }

    override fun onCreate() {
        super.onCreate()
        logger.i(TAG, "onCreate: Initializing channels...")
        notificationService.initializeChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logger.i(TAG, "onStartCommand: Starting foreground service...")

        val notification = notificationService.showTimerActiveNotification("Starting...")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1001,
                notification as android.app.Notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(1001, notification as android.app.Notification)
        }

        if (observationJob == null) {
            logger.i(TAG, "onStartCommand: Starting phase observation...")
            observationJob = observeTimerPhase()
                .onEach { phase ->
                    logger.d(TAG, "Phase emitted: $phase")
                    handlePhase(phase)
                }
                .launchIn(serviceScope)
        }
        return START_STICKY
    }

    // Change your variable at the top to track the CLASS, not the object
    private var lastNotifiedPhaseClass: Class<out TimerPhase>? = null

    private fun handlePhase(phase: TimerPhase) {
        // 1. SILENT COUNTDOWN: Update the persistent, quiet notification every second
        val displayTime = when (phase) {
            is TimerPhase.Active -> TimeFormatter.formatCountdown(phase.remainingMillis)
            is TimerPhase.LastMinute -> "${phase.remainingMillis / 1000}s until Zero Hour"
            is TimerPhase.Abort -> "🚨 ${phase.secondsRemaining} seconds to SOS!"
            is TimerPhase.Expired -> "SOS Dispatched."
            else -> "..."
        }
        notificationService.showTimerActiveNotification(displayTime)

        val currentPhaseClass = phase::class.java

        // 2. LOUD ALERTS: ONLY trigger when the phase class ACTUALLY changes (No spam!)
        if (currentPhaseClass != lastNotifiedPhaseClass) {
            when (phase) {
                is TimerPhase.HalfTime  -> notificationService.showHalfTimeNotification()
                is TimerPhase.Warning   -> notificationService.showWarningNotification()
                is TimerPhase.Critical  -> notificationService.showCriticalNotification()
                is TimerPhase.Imminent  -> notificationService.showImminentNotification()
                is TimerPhase.LastMinute -> notificationService.showLastMinuteNotification()
                is TimerPhase.Abort     -> notificationService.showZeroHourUpdate(phase.secondsRemaining) // ← loud, once
                is TimerPhase.Expired   -> {
                    notificationService.cancelZeroHourNotification()
                    feedbackService.stopEmergencyVibration()  // ← add this
                }
                else -> {}
            }
            lastNotifiedPhaseClass = currentPhaseClass
        }
        if (phase is TimerPhase.Abort) {
            notificationService.showZeroHourUpdate(phase.secondsRemaining) // ← silent update
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        logger.i(TAG, "onDestroy: Shutting down service.")
        serviceScope.cancel()
        super.onDestroy()
    }
}