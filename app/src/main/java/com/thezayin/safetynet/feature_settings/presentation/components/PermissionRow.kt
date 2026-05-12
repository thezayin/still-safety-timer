package com.thezayin.safetynet.feature_settings.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ui.components.GhostButton
import com.thezayin.safetynet.core.ui.theme.AuraMint
import com.thezayin.safetynet.core.ui.theme.AuraSunset
import com.thezayin.safetynet.core.ui.theme.AuraTypography

@Composable
fun PermissionRow(
    title: String,
    isReady: Boolean,
    icon: ImageVector,
    onFixClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isReady) AuraMint else AuraSunset,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = title,
            style = AuraTypography.bodyLarge,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        )

        if (isReady) {
            Text(
                text = stringResource(R.string.settings_status_ready).uppercase(),
                style = AuraTypography.labelLarge.copy(color = AuraMint)
            )
        } else {
            GhostButton(
                label = stringResource(R.string.settings_btn_fix),
                onClick = onFixClick,
                modifier = Modifier
                    .width(80.dp)
                    .height(40.dp)
            )
        }
    }
}