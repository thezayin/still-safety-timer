package com.thezayin.safetynet.core.infrastructure.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
        const val NOTIFICATION_ID_WARNING = 1002
        const val NOTIFICATION_ID_CRITICAL = 1003
        const val NOTIFICATION_ID_ZERO_HOUR = 1004
        const val NOTIFICATION_ID_EMERGENCY = 1005

        private const val TAG = "AndroidNotificationService"
    }

    override fun initializeChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val systemManager = appContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val channels = listOf(
            NotificationChannel(
                CHANNEL_ACTIVE_ID,
                "Timer Active",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Silent persistent foreground notice while safety timer is running"
                setShowBadge(false)
            },

            NotificationChannel(
                CHANNEL_ALERT_ID,
                "Check-in Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority reminders to check in"
                enableVibration(true)
            },

            NotificationChannel(
                CHANNEL_EMERGENCY_ID,
                "Emergency Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical alerts for Zero Hour and dispatched emergencies"
                enableVibration(true)
                setBypassDnd(true)
            }
        )

        systemManager.createNotificationChannels(channels)
        logger.i(TAG, "Notification channels initialised with high-priority safety guards")
    }

    private fun buildTapIntent(requestCode: Int, urgentRoute: String? = null): PendingIntent? {
        val intent = appContext.packageManager
            .getLaunchIntentForPackage(appContext.packageName)?.apply {
                if (urgentRoute != null) {
                    putExtra("NAV_DESTINATION", urgentRoute)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
            ?: return null

        return PendingIntent.getActivity(
            appContext,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun showTimerActiveNotification(remainingTimeText: String): Any {
        val notification = NotificationCompat.Builder(appContext, CHANNEL_ACTIVE_ID)
            .setSmallIcon(R.drawable.ic_still_logo)
            .setContentTitle("Safety Active")
            .setContentText("Safe for: $remainingTimeText")
            .setOngoing(true)
            .setSilent(true)
            .setContentIntent(buildTapIntent(NOTIFICATION_ID_ACTIVE, "ROUTE_TIMER"))
            .build()
        safeNotify(NOTIFICATION_ID_ACTIVE, notification)
        return notification
    }

    override fun showWarningNotification(): Any {
        val notification = NotificationCompat.Builder(appContext, CHANNEL_ALERT_ID)
            .setSmallIcon(R.drawable.ic_still_logo)
            .setContentTitle("Check in soon")
            .setContentText("Timer expires in 2 hours. Tap to check in.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(buildTapIntent(NOTIFICATION_ID_WARNING, "ROUTE_TIMER"))
            .build()
        safeNotify(NOTIFICATION_ID_WARNING, notification)
        return notification
    }

    override fun showCriticalNotification(): Any {
        val notification = NotificationCompat.Builder(appContext, CHANNEL_ALERT_ID)
            .setSmallIcon(R.drawable.ic_still_logo)
            .setContentTitle("Action Required")
            .setContentText("30 mins until circle is notified. Tap to reset.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(buildTapIntent(NOTIFICATION_ID_CRITICAL, "ROUTE_TIMER_CRITICAL"))
            .build()
        safeNotify(NOTIFICATION_ID_CRITICAL, notification)
        return notification
    }

    override fun showZeroHourNotification(): Any {
        val notification = NotificationCompat.Builder(appContext, CHANNEL_EMERGENCY_ID)
            .setSmallIcon(R.drawable.ic_still_logo)
            .setContentTitle("STILL · ZERO HOUR")
            .setContentText("60 seconds to confirm safety before circle is notified.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentIntent(buildTapIntent(NOTIFICATION_ID_ZERO_HOUR, "ROUTE_TIMER_ABORT"))
            .build()
        safeNotify(NOTIFICATION_ID_ZERO_HOUR, notification)
        return notification
    }

    override fun showEmergencyDispatchedNotification(): Any {
        val notification = NotificationCompat.Builder(appContext, CHANNEL_EMERGENCY_ID)
            .setSmallIcon(R.drawable.ic_still_logo)
            .setContentTitle("Alert Dispatched")
            .setContentText("Your emergency contacts have been notified.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(buildTapIntent(NOTIFICATION_ID_EMERGENCY, "ROUTE_TIMER"))
            .build()
        safeNotify(NOTIFICATION_ID_EMERGENCY, notification)
        return notification
    }

    override fun cancelAll() {
        notificationManager.cancelAll()
    }

    override fun cancelZeroHourNotification() {
        notificationManager.cancel(NOTIFICATION_ID_ZERO_HOUR)
    }

    private fun safeNotify(id: Int, notification: android.app.Notification) {
        try {
            notificationManager.notify(id, notification)
        } catch (e: SecurityException) {
            logger.e(TAG, "Permission denied for notification id=$id", e)
        }
    }
}