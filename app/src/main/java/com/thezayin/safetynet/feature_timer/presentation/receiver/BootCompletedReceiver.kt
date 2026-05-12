package com.thezayin.safetynet.feature_timer.presentation.receiver

import android.app.ForegroundServiceStartNotAllowedException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import com.thezayin.safetynet.feature_timer.domain.usecase.RestoreTimerUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val restoreTimer: RestoreTimerUseCase by inject()
    private val logger: LocalLogger by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                restoreTimer()
            } catch (e: Exception) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && e is ForegroundServiceStartNotAllowedException) {
                    logger.e(
                        "BootReceiver",
                        "OS blocked background service start. App must be opened by user.",
                        e
                    )
                } else if (e is IllegalStateException) {
                    logger.e(
                        "BootReceiver", "Illegal state when restoring timer from background", e
                    )
                } else {
                    logger.e("BootReceiver", "Unknown error during timer restore", e)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}