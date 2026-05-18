package com.thezayin.safetynet.feature_timer.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thezayin.safetynet.MainActivity
import com.thezayin.safetynet.core.ads.manager.AdManager
import com.thezayin.safetynet.core.ads.model.AdType
import com.thezayin.safetynet.core.ads.native_ad.NativeAdComposable
import com.thezayin.safetynet.core.ui.theme.TwilightBackground
import com.thezayin.safetynet.feature_timer.presentation.component.BatteryExplanationDialog
import com.thezayin.safetynet.feature_timer.presentation.component.EmergencyAbortDialog
import com.thezayin.safetynet.feature_timer.presentation.component.TimerContent
import com.thezayin.safetynet.feature_timer.presentation.contract.TimerEffect
import com.thezayin.safetynet.feature_timer.presentation.contract.TimerIntent
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@SuppressLint("BatteryLife")
@Composable
fun TimerScreen(
    viewModel: TimerViewModel = koinViewModel(),
    onNavigateToSettings: () -> Unit,
    adManager: AdManager = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showAbortDialog by remember { mutableStateOf(false) }
    var showBatteryExplainer by remember { mutableStateOf(false) }
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        adManager.load(AdType.NATIVE_HOME)
        adManager.load(AdType.INTERSTITIAL)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onIntent(TimerIntent.RefreshWarningState)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val handleIntent: (TimerIntent) -> Unit = { intent ->
        if (intent is TimerIntent.StartTimer && activity != null) {
            (activity as? MainActivity)?.suppressNextAppOpen()
            adManager.show(AdType.INTERSTITIAL, activity) {
                // Ad dismissed (or failed) — now actually start the timer
                viewModel.onIntent(intent)
                // Pre-load next interstitial for the next session
                adManager.load(AdType.INTERSTITIAL)
            }
        } else {
            viewModel.onIntent(intent)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TimerEffect.TriggerBatteryExemptionPopup -> {
                    val intent =
                        Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                            data = "package:${context.packageName}".toUri()
                        }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        showBatteryExplainer = true
                    }
                }

                TimerEffect.OpenExactAlarmSettings -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = "package:${context.packageName}".toUri()
                        }
                        context.startActivity(intent)
                    }
                }

                is TimerEffect.ShowError -> Toast.makeText(
                    context, effect.message, Toast.LENGTH_LONG
                ).show()

                TimerEffect.NavigateToSettings -> onNavigateToSettings()
                TimerEffect.TriggerSuccessHaptic -> { /* Trigger haptic here */
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TwilightBackground)
    ) {
        TimerContent(
            state = state,
            onIntent = handleIntent,
            onShowDeactivateDialog = { showAbortDialog = true },
            onWarningClick = { showBatteryExplainer = true },
            nativeAdContent = {
                NativeAdComposable(
                    adType = AdType.NATIVE_HOME,
                    adManager = adManager
                )
            }
        )

        EmergencyAbortDialog(isVisible = showAbortDialog, onConfirm = {
            showAbortDialog = false
            viewModel.onIntent(TimerIntent.StopTimer)
        }, onDismiss = { showAbortDialog = false })

        BatteryExplanationDialog(isVisible = showBatteryExplainer, onConfirm = {
            showBatteryExplainer = false
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            context.startActivity(intent)
        }, onDismiss = { showBatteryExplainer = false })
    }
}