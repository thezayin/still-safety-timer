package com.thezayin.safetynet.feature_timer.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thezayin.safetynet.core.ui.theme.TwilightBackground
import com.thezayin.safetynet.feature_timer.presentation.component.BatteryExplanationDialog
import com.thezayin.safetynet.feature_timer.presentation.component.EmergencyAbortDialog
import com.thezayin.safetynet.feature_timer.presentation.component.TimerContent
import com.thezayin.safetynet.feature_timer.presentation.contract.TimerEffect
import com.thezayin.safetynet.feature_timer.presentation.contract.TimerIntent
import org.koin.androidx.compose.koinViewModel

@SuppressLint("BatteryLife")
@Composable
fun TimerScreen(
    viewModel: TimerViewModel = koinViewModel(),
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showAbortDialog by remember { mutableStateOf(false) }
    var showBatteryExplainer by remember { mutableStateOf(false) }

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
                    context,
                    effect.message,
                    Toast.LENGTH_LONG
                ).show()

                TimerEffect.NavigateToSettings -> onNavigateToSettings()
                TimerEffect.TriggerSuccessHaptic -> { /* Trigger haptic here */
                }
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(TwilightBackground)) {
        TimerContent(
            state = state,
            onIntent = { viewModel.onIntent(it) },
            onShowDeactivateDialog = { showAbortDialog = true },
            onWarningClick = { showBatteryExplainer = true }
        )

        EmergencyAbortDialog(
            isVisible = showAbortDialog,
            onConfirm = {
                showAbortDialog = false
                viewModel.onIntent(TimerIntent.StopTimer)
            },
            onDismiss = { showAbortDialog = false }
        )

        BatteryExplanationDialog(
            isVisible = showBatteryExplainer,
            onConfirm = {
                showBatteryExplainer = false
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                context.startActivity(intent)
            },
            onDismiss = { showBatteryExplainer = false }
        )
    }
}