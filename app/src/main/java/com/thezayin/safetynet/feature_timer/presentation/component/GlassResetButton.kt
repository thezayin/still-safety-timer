package com.thezayin.safetynet.feature_timer.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thezayin.safetynet.core.ui.theme.AuraCoral
import com.thezayin.safetynet.core.ui.theme.TextCloud
import com.thezayin.safetynet.core.ui.theme.TwilightBackground

@Composable
fun GlassResetButton(
    label: String, onClick: () -> Unit, modifier: Modifier = Modifier, isCritical: Boolean = false
) {
    val haptic = LocalHapticFeedback.current

    val baseColor = if (isCritical) AuraCoral else TextCloud

    Box(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .height(64.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(baseColor.copy(alpha = 0.05f))
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }, contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.uppercase(),
            color = baseColor.copy(alpha = 0.9f),
            fontWeight = FontWeight.Light,
            letterSpacing = 5.sp,
            fontSize = 12.sp
        )
    }
}

@Preview(name = "Normal Action", showBackground = true, backgroundColor = 0xFF151921)
@Composable
fun GlassResetButtonPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TwilightBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassResetButton(
            label = "Begin Stillness", onClick = {})
    }
}

@Preview(name = "Critical Action", showBackground = true, backgroundColor = 0xFF151921)
@Composable
fun GlassResetButtonCriticalPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TwilightBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassResetButton(
            label = "Abort SOS Dispatch", onClick = {}, isCritical = true
        )
    }
}