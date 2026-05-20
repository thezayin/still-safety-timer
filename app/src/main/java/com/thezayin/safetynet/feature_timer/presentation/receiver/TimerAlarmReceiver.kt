package com.thezayin.safetynet.feature_timer.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.thezayin.safetynet.feature_timer.data.repository.TimerHardwareManagerImpl
import com.thezayin.safetynet.feature_timer.domain.usecase.TriggerZeroHourUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TimerAlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val triggerZeroHour: TriggerZeroHourUseCase by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        Log.i("TimerAlarmReceiver", "WAKE UP: Alarm received with action: ${intent?.action}")

        if (intent?.action != TimerHardwareManagerImpl.ACTION_ZERO_HOUR) {
            Log.w("TimerAlarmReceiver", "Ignored broadcast with action: ${intent?.action}")
            return
        }

        Log.i("TimerAlarmReceiver", "Zero Hour triggered! Acquiring WakeLock...")

        val pendingResult = goAsync()
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "SafetyNet::ZeroHourWakeLock"
        ).apply {
            acquire(60_000L)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // This calls hardwareManager.enqueueEmergencyWorker()
                triggerZeroHour()
            } catch (e: Exception) {
                Log.e("TimerAlarmReceiver", "Failed to trigger Zero Hour", e)
            } finally {
                if (wakeLock.isHeld) {
                    wakeLock.release()
                }
                pendingResult.finish()
                Log.i("TimerAlarmReceiver", "WakeLock released, Emergency Worker should be running now.")
            }
        }
    }
}