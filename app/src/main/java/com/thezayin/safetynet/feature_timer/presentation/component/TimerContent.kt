package com.thezayin.safetynet.feature_timer.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ui.theme.AuraCoral
import com.thezayin.safetynet.core.ui.theme.SplashBlack
import com.thezayin.safetynet.core.ui.theme.TextCloud
import com.thezayin.safetynet.core.ui.theme.TwilightBackground
import com.thezayin.safetynet.feature_timer.domain.model.TimerPhase
import com.thezayin.safetynet.feature_timer.presentation.contract.TimerIntent
import com.thezayin.safetynet.feature_timer.presentation.contract.TimerState

@Composable
fun TimerContent(
    state: TimerState,
    onIntent: (TimerIntent) -> Unit,
    onShowDeactivateDialog: () -> Unit,
    onWarningClick: () -> Unit,
    modifier: Modifier = Modifier,
    nativeAdContent: @Composable () -> Unit = {}
) {
    Scaffold(
        modifier = modifier, containerColor = TwilightBackground, bottomBar = {
            val isSystemActive = state.phase !is TimerPhase.Idle
            val isAbortPhase = state.phase is TimerPhase.Abort
            if (isSystemActive && !isAbortPhase) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.action_deactivate_system),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextCloud.copy(alpha = 0.2f),
                        modifier = Modifier.clickable { onShowDeactivateDialog() },
                        letterSpacing = 2.sp
                    )
                }
            }
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(vertical = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isBatterySaverAlertVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AuraCoral)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.warning_battery_saver_timer),
                        color = SplashBlack,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StillStreakIndicator(count = state.streakCount)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onIntent(TimerIntent.OnSettingsClicked) },
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height(1.dp)
                                .background(TextCloud.copy(alpha = 0.4f))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(10.dp)
                                .height(1.dp)
                                .background(TextCloud.copy(alpha = 0.4f))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            if (state.isBatteryWarningActive) {
                SafetyWarningBanner(
                    onClick = onWarningClick, modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            StillAuraOrb(phase = state.phase, displayTime = state.displayTime)
            Spacer(modifier = Modifier.height(24.dp))
            PhaseStatusBadge(
                status = when (state.phase) {
                    is TimerPhase.Active -> stringResource(R.string.status_armed)
                    is TimerPhase.Warning -> stringResource(R.string.status_warning)
                    is TimerPhase.Critical -> stringResource(R.string.status_critical)
                    is TimerPhase.Abort -> stringResource(R.string.status_abort)
                    is TimerPhase.Expired -> stringResource(R.string.status_expired)
                    else -> stringResource(R.string.status_idle)
                }, visible = state.phase !is TimerPhase.Idle
            )
            Spacer(modifier = Modifier.weight(1.2f))
            val isSystemActive = state.phase !is TimerPhase.Idle
            val isAbortPhase = state.phase is TimerPhase.Abort
            GlassResetButton(
                label = when {
                    isAbortPhase -> stringResource(R.string.action_i_am_safe)
                    isSystemActive -> stringResource(R.string.action_check_in)
                    else -> stringResource(R.string.action_begin_stillness)
                }, onClick = {
                    when {
                        isAbortPhase -> onIntent(TimerIntent.AbortEmergency)
                        isSystemActive -> onIntent(TimerIntent.CheckIn)
                        else -> {
                            onIntent(TimerIntent.StartTimer(ignoreBatteryWarning = false))
                        }
                    }
                }, isCritical = state.phase is TimerPhase.Critical || isAbortPhase
            )
            Spacer(modifier = Modifier.height(24.dp))
            nativeAdContent()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerContentPreview() {
    TimerContent(
        state = TimerState(
            phase = TimerPhase.Idle,
            displayTime = "01:00:00",
            streakCount = 4,
            isBatteryWarningActive = false,
            isBatterySaverAlertVisible = false
        ),
        onIntent = {},
        onShowDeactivateDialog = {},
        onWarningClick = {},
        nativeAdContent = {

        })
}