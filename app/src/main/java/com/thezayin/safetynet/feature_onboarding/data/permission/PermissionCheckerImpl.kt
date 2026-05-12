package com.thezayin.safetynet.feature_onboarding.data.permission

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.thezayin.safetynet.feature_onboarding.domain.permission.PermissionChecker
import com.thezayin.safetynet.feature_onboarding.domain.permission.SafetyPermissionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PermissionCheckerImpl(
    private val context: Context
) : PermissionChecker {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun getActualSystemStatus(): SafetyPermissionStatus {
        val notifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            androidx.core.app.NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        val alarms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        return SafetyPermissionStatus(notifications, alarms)
    }

    private val _status = MutableStateFlow(getActualSystemStatus())
    override fun observePermissionStatus(): Flow<SafetyPermissionStatus> = _status.asStateFlow()

    override fun checkAllPermissions(): SafetyPermissionStatus {
        val currentStatus = getActualSystemStatus()
        _status.update { currentStatus }
        return currentStatus
    }
}