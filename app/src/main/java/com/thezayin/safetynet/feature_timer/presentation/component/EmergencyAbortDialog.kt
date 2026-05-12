package com.thezayin.safetynet.feature_timer.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ui.theme.*

@Composable
fun EmergencyAbortDialog(
    isVisible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = TwilightSurface,
            titleContentColor = TextCloud,
            textContentColor = TextSlate,
            modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(28.dp)),
            title = {
                Text(
                    text = stringResource(R.string.dialog_abort_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = stringResource(R.string.dialog_abort_message))
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AuraCoral,
                        contentColor = SplashBlack
                    )
                ) {
                    Text(
                        text = stringResource(R.string.dialog_action_yes_deactivate),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.dialog_action_cancel),
                        color = TextCloud
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun EmergencyAbortDialogPreview() {
    EmergencyAbortDialog(isVisible = true, onConfirm = {}, onDismiss = {})
}