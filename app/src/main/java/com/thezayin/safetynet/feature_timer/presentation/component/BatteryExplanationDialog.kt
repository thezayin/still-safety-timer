package com.thezayin.safetynet.feature_timer.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ui.theme.AuraMint
import com.thezayin.safetynet.core.ui.theme.TextCloud
import com.thezayin.safetynet.core.ui.theme.TwilightBackground

@Composable
fun BatteryExplanationDialog(
    isVisible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.battery_dialog_title),
                color = TextCloud
            )
        },
        text = {
            Text(
                text = stringResource(R.string.battery_dialog_desc),
                color = TextCloud.copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.battery_dialog_confirm), color = AuraMint)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.battery_dialog_cancel), color = TextCloud.copy(alpha = 0.5f))
            }
        }
    )
}

@Preview
@Composable
private fun BatteryExplanationDialogPreview() {
    Box(modifier = Modifier.background(TwilightBackground).padding(20.dp)) {
        BatteryExplanationDialog(
            isVisible = true,
            onConfirm = {},
            onDismiss = {}
        )
    }
}