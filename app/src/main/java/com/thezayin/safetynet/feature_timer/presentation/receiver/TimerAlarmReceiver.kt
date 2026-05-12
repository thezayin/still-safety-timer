package com.thezayin.safetynet.feature_timer.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
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
        if (intent?.action != TimerHardwareManagerImpl.ACTION_ZERO_HOUR) return

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
                triggerZeroHour()
            } finally {
                if (wakeLock.isHeld) {
                    wakeLock.release()
                }
                pendingResult.finish()
            }
        }
    }
}