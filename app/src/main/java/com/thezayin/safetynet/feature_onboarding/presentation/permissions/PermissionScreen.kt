package com.thezayin.safetynet.feature_onboarding.presentation.permissions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thezayin.safetynet.MainActivity
import com.thezayin.safetynet.core.ads.manager.AdManager
import com.thezayin.safetynet.core.ads.model.AdType
import com.thezayin.safetynet.core.ads.native_ad.NativeAdComposable
import com.thezayin.safetynet.core.presentation.util.ObserveEffect
import com.thezayin.safetynet.feature_onboarding.presentation.permissions.components.PermissionContent
import com.thezayin.safetynet.feature_onboarding.presentation.permissions.mvi.PermissionEffect
import com.thezayin.safetynet.feature_onboarding.presentation.permissions.mvi.PermissionIntent
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun PermissionScreen(
    onNavigateNext: () -> Unit,
    viewModel: PermissionViewModel = koinViewModel(),
    adManager: AdManager = koinInject()

) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        adManager.load(AdType.NATIVE_HOME)
        adManager.load(AdType.INTERSTITIAL)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onIntent(PermissionIntent.RefreshStatus)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { viewModel.onIntent(PermissionIntent.RefreshStatus) }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            PermissionEffect.TriggerNotificationPopup -> {
                (activity as? MainActivity)?.suppressNextAppOpen()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            PermissionEffect.OpenAlarmSettings -> {
                (activity as? MainActivity)?.suppressNextAppOpen()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            }

            PermissionEffect.NavigateToProfile -> {
                if (activity != null) {
                    (activity as? MainActivity)?.suppressNextAppOpen()
                    adManager.show(AdType.INTERSTITIAL, activity) {
                        onNavigateNext()
                    }
                } else {
                    onNavigateNext()
                }

            }
        }
    }
    PermissionContent(
        isNotificationGranted = state.status.notifications,
        isExactAlarmGranted = state.status.exactAlarms,
        canProceed = state.status.isAllGranted,
        onNotificationClick = { viewModel.onIntent(PermissionIntent.RequestNotification) },
        onAlarmClick = { viewModel.onIntent(PermissionIntent.RequestAlarm) },
        onContinueClick = { viewModel.onIntent(PermissionIntent.OnContinueClicked) },
        nativeAdContent = {
            NativeAdComposable(
                adType = AdType.NATIVE_HOME, adManager = adManager
            )
        })
}