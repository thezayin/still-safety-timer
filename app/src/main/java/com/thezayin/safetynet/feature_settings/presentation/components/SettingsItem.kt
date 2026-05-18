package com.thezayin.safetynet.feature_settings.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ui.theme.AuraMint
import com.thezayin.safetynet.core.ui.theme.AuraTypography

@Composable
fun SettingsItem(
    label: String,
    value: String,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = AuraTypography.labelSmall,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = AuraTypography.bodyLarge
            )
        }

        Text(
            text = stringResource(R.string.settings_btn_edit).uppercase(),
            style = AuraTypography.labelLarge,
            color = AuraMint,
            modifier = Modifier
                .clickable { onEditClick() }
                .padding(8.dp)
        )
    }
}