package com.thezayin.safetynet.feature_onboarding.presentation.permissions

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thezayin.safetynet.core.presentation.util.ObserveEffect
import com.thezayin.safetynet.feature_onboarding.presentation.permissions.components.PermissionContent
import com.thezayin.safetynet.feature_onboarding.presentation.permissions.mvi.PermissionEffect
import com.thezayin.safetynet.feature_onboarding.presentation.permissions.mvi.PermissionIntent
import org.koin.androidx.compose.koinViewModel

@Composable
fun PermissionScreen(
    onNavigateNext: () -> Unit,
    viewModel: PermissionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { viewModel.onIntent(PermissionIntent.RefreshStatus) }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            PermissionEffect.TriggerNotificationPopup -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            PermissionEffect.OpenAlarmSettings -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            }

            PermissionEffect.NavigateToProfile -> onNavigateNext()
        }
    }
    PermissionContent(
        isNotificationGranted = state.status.notifications,
        isExactAlarmGranted = state.status.exactAlarms,
        canProceed = state.status.isAllGranted,
        onNotificationClick = { viewModel.onIntent(PermissionIntent.RequestNotification) },
        onAlarmClick = { viewModel.onIntent(PermissionIntent.RequestAlarm) },
        onContinueClick = { viewModel.onIntent(PermissionIntent.OnContinueClicked) }
    )
}