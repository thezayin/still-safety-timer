package com.thezayin.safetynet.feature_onboarding.presentation.permissions.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ui.components.PrimaryButton
import com.thezayin.safetynet.core.ui.components.StillAuraBackground
import com.thezayin.safetynet.core.ui.theme.SafetyNetTheme
import com.thezayin.safetynet.feature_onboarding.presentation.consent.components.EntranceAnimation

@Composable
fun PermissionContent(
    isNotificationGranted: Boolean,
    isExactAlarmGranted: Boolean,
    canProceed: Boolean,
    onNotificationClick: () -> Unit,
    onAlarmClick: () -> Unit,
    onContinueClick: () -> Unit,
    nativeAdContent: @Composable () -> Unit = {}
) {
    SafetyNetTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            StillAuraBackground()

            Scaffold(
                containerColor = Color.Transparent, bottomBar = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EntranceAnimation(delay = 1000) {
                            PrimaryButton(
                                text = stringResource(id = R.string.permission_button),
                                onClick = onContinueClick,
                                enabled = canProceed,
                                modifier = Modifier
                                    .padding(24.dp)
                                    .navigationBarsPadding()
                            )
                        }
                        nativeAdContent()
                    }
                }) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 24.dp)
                        .statusBarsPadding()
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                    EntranceAnimation(delay = 0) {
                        Text(
                            text = stringResource(id = R.string.permission_title),
                            style = MaterialTheme.typography.displayLarge
                        )
                    }

                    EntranceAnimation(delay = 200) {
                        Text(
                            text = stringResource(id = R.string.permission_description),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                    EntranceAnimation(delay = 400) {
                        PermissionActionRow(
                            title = stringResource(id = R.string.permission_notif_title),
                            icon = Icons.Default.Notifications,
                            isGranted = isNotificationGranted,
                            onClick = onNotificationClick,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                    EntranceAnimation(delay = 550) {
                        PermissionActionRow(
                            title = stringResource(id = R.string.permission_alarm_title),
                            icon = Icons.Default.Timer,
                            isGranted = isExactAlarmGranted,
                            onClick = onAlarmClick,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PermissionContentLuxuryPreview() {
    PermissionContent(
        isNotificationGranted = true,
        isExactAlarmGranted = false,
        canProceed = true,
        onNotificationClick = {},
        onAlarmClick = {},
        onContinueClick = {})
}