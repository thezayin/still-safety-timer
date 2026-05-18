package com.thezayin.safetynet.core.infrastructure.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import com.thezayin.safetynet.core.domain.notification.AppNotificationService

class AndroidNotificationService(
    context: Context,
    private val logger: LocalLogger
) : AppNotificationService {

    private val appContext = context.applicationContext
    private val notificationManager = NotificationManagerCompat.from(appContext)

    companion object {
        const val CHANNEL_ACTIVE_ID = "channel_timer_active"
        const val CHANNEL_ALERT_ID = "channel_timer_alert"
        const val CHANNEL_EMERGENCY_ID = "channel_timer_emergency"

        const val NOTIFICATION_ID_ACTIVE = 1001
        const val NOTIFICATION_ID_HALF_TIME = 1002
        const val NOTIFICATION_ID_WARNING = 1003
        const val NOTIFICATION_ID_CRITICAL = 1004
        const val NOTIFICATION_ID_IMMINENT = 1005
        const val NOTIFICATION_ID_LAST_MINUTE = 1006
        const val NOTIFICATION_ID_ZERO_HOUR = 1007
        const val NOTIFICATION_ID_EMERGENCY = 1008
    }

    override fun initializeChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val systemManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Active: Low priority, ongoing (Foreground)
        val activeChannel = NotificationChannel(
            CHANNEL_ACTIVE_ID,
            "Timer Active",
            NotificationManager.IMPORTANCE_LOW
        )

        // Alerts: High priority, shows peek/heads-up
        val alertChannel = NotificationChannel(
            CHANNEL_ALERT_ID,
            "Check-in Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)
        }

        // Emergency: Max priority, bypasses DND
        val emergencyChannel = NotificationChannel(
            CHANNEL_EMERGENCY_ID,
            "Emergency Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)
            setBypassDnd(true)
        }

        systemManager.createNotificationChannels(
            listOf(
                activeChannel,
                alertChannel,
                emergencyChannel
            )
        )
    }

    override fun showTimerActiveNotification(remainingTimeText: String): Any {
        val builder = NotificationCompat.Builder(appContext, CHANNEL_ACTIVE_ID)
            .setSmallIcon(R.drawable.ic_still_logo)
            .setContentTitle("Safety Active")
            .setContentText("Safe for: $remainingTimeText")
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        val notification = builder.build()
        safeNotify(NOTIFICATION_ID_ACTIVE, notification)
        return notification
    }

    override fun showHalfTimeNotification(): Any {
        val builder = NotificationCompat.Builder(appContext, CHANNEL_ALERT_ID)
            .setSmallIcon(R.drawable.ic_still_logo)
            .setContentTitle("Halfway there")
            .setContentText("Half your safety window has passed.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notification = builder.build()
        safeNotify(NOTIFICATION_ID_HALF_TIME, notification)
        return notification
    }

    override fun showWarningNotification(): Any {
        val builder = NotificationCompat.Builder(appContext, CHANNEL_ALERT_ID)
            .setSmallIcon(R.drawable.ic_still_logo)
            .setContentTitle("6 hours remaining")
            .setContentText("Remember to check in before your timer expires.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notification = builder.build()
        safeNotify(NOTIFICATION_ID_WARNING, notification)
        return notification
    }

    override fun showCriticalNotification(): Any {
        val builder = NotificationCompat.Builder(appContext, CHANNEL_ALERT_ID)
            .setSmallIcon(R.drawable.ic_still_logo)
            .setContentTitle("1 hour remaining")
            .setContentText("Check in now to reset your safety timer.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notification = builder.build()
        safeNotify(NOTIFICATION_ID_CRITICAL, notification)
        return notification
    }

    override fun showImminentNotification(): Any {
        val builder = NotificationCompat.Builder(appContext, CHANNEL_ALERT_ID)
            .setSmallIcon(R.drawable.ic_still_logo)
            .setContentTitle("30 minutes remaining")
            .setContentText("Your circle will be notified soon. Tap to check in.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notification = builder.build()
        safeNotify(NOTIFICATION_ID_IMMINENT, notification)
        return notification
    }

    override fun showLastMinuteNotification(): Any {
        val builder = NotificationCompat.Builder(appContext, CHANNEL_EMERGENCY_ID)
            .setSmallIcon(R.drawable.ic_still_logo)
            .setContentTitle("⚠️ 1 minute remaining")
            .setContentText("Check in immediately or your emergency contacts will be alerted.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(null, true) // Force heads-up

        val notification = builder.build()
        safeNotify(NOTIFICATION_ID_LAST_MINUTE, notification)
        return notification
    }

// In AndroidNotificationService.kt

    override fun showZeroHourAlertOnce(): Any {
        val builder = NotificationCompat.Builder(appContext, CHANNEL_EMERGENCY_ID)
            .setSmallIcon(R.drawable.ic_still_logo)
            .setContentTitle("⚠️ STILL · ZERO HOUR")
            .setContentText("60 seconds until emergency dispatch.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(null, true)
        // No sound/vibrate suppression here — let the channel do its job

        val notification = builder.build()
        safeNotify(NOTIFICATION_ID_ZERO_HOUR, notification)
        return notification
    }

    override fun showZeroHourUpdate(secondsRemaining: Int): Any {
        val builder = NotificationCompat.Builder(appContext, CHANNEL_EMERGENCY_ID)
            .setSmallIcon(R.drawable.ic_still_logo)
            .setContentTitle("⚠️ STILL · ZERO HOUR")
            .setContentText("$secondsRemaining seconds until emergency dispatch.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOnlyAlertOnce(true)  // silent after first
            .setSound(null)
            .setVibrate(null)
            .setDefaults(0)

        val notification = builder.build()
        safeNotify(NOTIFICATION_ID_ZERO_HOUR, notification)
        return notification
    }

    override fun showEmergencyDispatchedNotification(): Any {
        val builder = NotificationCompat.Builder(appContext, CHANNEL_EMERGENCY_ID)
            .setSmallIcon(R.drawable.ic_still_logo)
            .setContentTitle("Alert Dispatched")
            .setContentText("Your emergency contacts have been notified.")
            .setPriority(NotificationCompat.PRIORITY_MAX)

        val notification = builder.build()
        safeNotify(NOTIFICATION_ID_EMERGENCY, notification)
        return notification
    }

    override fun cancelAll() = notificationManager.cancelAll()
    override fun cancelZeroHourNotification() {
        notificationManager.cancel(NOTIFICATION_ID_ZERO_HOUR)
        notificationManager.cancel(NOTIFICATION_ID_LAST_MINUTE)
    }

    private fun safeNotify(id: Int, notification: android.app.Notification) {
        try {
            notificationManager.notify(id, notification)
        } catch (e: SecurityException) {
            logger.e("NotificationService", "Permission POST_NOTIFICATIONS missing", e)
        }
    }
}