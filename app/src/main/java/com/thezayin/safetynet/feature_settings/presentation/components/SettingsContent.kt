package com.thezayin.safetynet.feature_settings.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ui.components.GhostButton
import com.thezayin.safetynet.core.ui.theme.SafetyNetTheme

@Composable
fun SettingsContent(
    userName: String,
    contactName: String,
    contactEmail: String,
    interval: Int,
    isNotificationsReady: Boolean,
    isAlarmsReady: Boolean,
    onBackClick: () -> Unit,
    onEditName: () -> Unit,
    onEditContactName: () -> Unit,
    onEditContactEmail: () -> Unit,
    onChangeInterval: () -> Unit,
    onFixNotification: () -> Unit,
    onFixAlarms: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit,
    onContactUs: () -> Unit,
    onWipeData: () -> Unit,
    adContent: @Composable () -> Unit = { AdContainer() },
) {
    Scaffold(
        topBar = {
            GlassTopBar(
                title = stringResource(R.string.settings_label_name),
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            item {
                SettingsSection(title = stringResource(R.string.settings_section_identity)) {
                    SettingsItem(stringResource(R.string.settings_label_name), userName, onEditName)
                }
            }

            item {
                SettingsSection(title = stringResource(R.string.settings_section_guardian)) {
                    SettingsItem(
                        stringResource(R.string.settings_label_contact_name),
                        contactName,
                        onEditContactName
                    )
                    SettingsItem(
                        stringResource(R.string.settings_label_contact_email),
                        contactEmail,
                        onEditContactEmail
                    )
                }
            }

            item {
                SettingsSection(title = stringResource(R.string.settings_section_pulse)) {
                    SettingsItem(
                        stringResource(R.string.settings_label_frequency),
                        stringResource(R.string.settings_label_hours_val, interval),
                        onChangeInterval
                    )
                }
            }

            item { adContent() }

            item {
                SettingsSection(title = stringResource(R.string.settings_section_shield)) {
                    PermissionRow(
                        title = stringResource(R.string.settings_label_notifications),
                        isReady = isNotificationsReady,
                        icon = Icons.Default.Notifications,
                        onFixClick = onFixNotification
                    )
                    PermissionRow(
                        title = stringResource(R.string.settings_label_alarms),
                        isReady = isAlarmsReady,
                        icon = Icons.Default.Timer,
                        onFixClick = onFixAlarms
                    )
                }
            }

            item {
                SettingsSection(title = stringResource(R.string.settings_section_support)) {
                    SupportRow(
                        stringResource(R.string.settings_label_privacy),
                        Icons.Default.PrivacyTip,
                        onPrivacyClick
                    )
                    SupportRow(
                        stringResource(R.string.settings_label_terms),
                        Icons.Default.Description,
                        onTermsClick
                    )
                    SupportRow(
                        stringResource(R.string.settings_label_support),
                        Icons.Default.Email,
                        onContactUs
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                GhostButton(
                    label = stringResource(R.string.settings_btn_wipe),
                    onClick = onWipeData,
                    isCritical = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsContentPreview() {
    SafetyNetTheme {
        SettingsContent(
            userName = "John Doe",
            contactName = "Jane Smith",
            contactEmail = "jane.smith@example.com",
            interval = 4,
            isNotificationsReady = true,
            isAlarmsReady = false,
            onBackClick = {},
            onEditName = {},
            onEditContactName = {},
            onEditContactEmail = {},
            onChangeInterval = {},
            onFixNotification = {},
            onFixAlarms = {},
            onPrivacyClick = {},
            onTermsClick = {},
            onContactUs = {},
            onWipeData = {},
            adContent = {}
        )
    }
}