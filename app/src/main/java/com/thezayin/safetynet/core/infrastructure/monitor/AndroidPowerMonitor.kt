package com.thezayin.safetynet.core.infrastructure.monitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import androidx.core.content.ContextCompat
import com.thezayin.safetynet.core.domain.monitor.PowerMonitor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class AndroidPowerMonitor(context: Context) : PowerMonitor {

    private val appContext = context.applicationContext
    private val powerManager = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager

    override fun isBatterySaverEnabled(): Boolean =
        powerManager.isPowerSaveMode

    override fun isIgnoringBatteryOptimizations(): Boolean =
        powerManager.isIgnoringBatteryOptimizations(appContext.packageName)

    override val batterySaverState: Flow<Boolean> = callbackFlow {
        trySend(powerManager.isPowerSaveMode)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
                    trySend(powerManager.isPowerSaveMode)
                }
            }
        }

        val filter = IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        ContextCompat.registerReceiver(
            appContext,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        awaitClose {
            appContext.unregisterReceiver(receiver)
        }
    }.distinctUntilChanged()
}