package com.thezayin.safetynet.feature_settings.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ui.components.GhostButton
import com.thezayin.safetynet.core.ui.components.PrimaryButton
import com.thezayin.safetynet.core.ui.components.PrimaryTextField
import com.thezayin.safetynet.core.ui.theme.AuraTypography

@Composable
fun EditFieldBottomSheet(
    title: String,
    initialValue: String,
    onSave: (String) -> Unit,
    onCancel: () -> Unit,
    validate: (String) -> Boolean
) {
    var textState by remember { mutableStateOf(initialValue) }
    val isChangedAndValid = remember(textState) {
        validate(textState) && textState.trim() != initialValue.trim()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = title.uppercase(),
            style = AuraTypography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        PrimaryTextField(
            value = textState,
            onValueChange = { textState = it },
            placeholder = stringResource(R.string.settings_placeholder_edit),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = stringResource(R.string.settings_btn_save),
            onClick = { onSave(textState.trim()) },
            enabled = isChangedAndValid,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        GhostButton(
            label = stringResource(R.string.settings_btn_cancel),
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        )
    }
}