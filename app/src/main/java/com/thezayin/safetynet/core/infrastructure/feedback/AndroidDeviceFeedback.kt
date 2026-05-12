package com.thezayin.safetynet.core.infrastructure.feedback

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.thezayin.safetynet.core.domain.feedback.DeviceFeedbackService
import com.thezayin.safetynet.core.domain.logger.LocalLogger

class AndroidDeviceFeedback(
    context: Context,
    private val logger: LocalLogger
) : DeviceFeedbackService {
    private val appContext = context.applicationContext

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = appContext.getSystemService(
                Context.VIBRATOR_MANAGER_SERVICE
            ) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun triggerCheckInHaptic() {
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    50L,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50L)
        }
    }

    override fun triggerEmergencyVibration() {
        if (!vibrator.hasVibrator()) return

        val pattern = longArrayOf(0L, 600L, 300L, 600L, 300L)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(pattern, /* repeat = */ 0)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, /* repeat = */ 0)
        }
    }

    override fun stopEmergencyVibration() {
        try {
            vibrator.cancel()
        } catch (e: SecurityException) {
            logger.e("AndroidDeviceFeedback", "Could not cancel vibration", e)
        }
    }
}