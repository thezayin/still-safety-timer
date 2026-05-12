package com.thezayin.safetynet.feature_settings.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ui.components.PrimaryButton
import com.thezayin.safetynet.core.ui.theme.AuraMint
import com.thezayin.safetynet.core.ui.theme.AuraTypography

@Composable
fun IntervalSelectorSheet(
    currentInterval: Int,
    onSave: (Int) -> Unit
) {
    var selected by remember { mutableIntStateOf(currentInterval) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp)
        .navigationBarsPadding()) {
        Text(
            text = stringResource(R.string.settings_title_pulse),
            style = AuraTypography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IntervalTile(24, selected == 24, { selected = 24 }, Modifier.weight(1f))
            IntervalTile(48, selected == 48, { selected = 48 }, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = stringResource(R.string.settings_btn_update_frequency),
            onClick = { onSave(selected) },
            enabled = selected != currentInterval,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun IntervalTile(hours: Int, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(100.dp)
            .border(
                width = 1.dp,
                color = if (isSelected) AuraMint else AuraMint.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_label_hours, hours),
            style = AuraTypography.bodyLarge,
            color = if (isSelected) AuraMint else AuraTypography.bodyLarge.color
        )
    }
}