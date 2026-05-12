package com.thezayin.safetynet.core.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.thezayin.safetynet.core.ui.theme.AuraMint
import com.thezayin.safetynet.core.ui.theme.SafetyNetTheme
import com.thezayin.safetynet.core.ui.theme.StillAnimation
import com.thezayin.safetynet.core.ui.theme.TwilightBackground

@Composable
fun StillAuraBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "AuraPulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = StillAnimation.stillTween(StillAnimation.LongDuration * 3),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(color = TwilightBackground)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AuraMint.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(size.width * 0.2f, size.height * 0.2f),
                radius = size.minDimension * scale
            ),
            center = Offset(size.width * 0.2f, size.height * 0.2f),
            radius = size.minDimension * scale
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AuraMint.copy(alpha = 0.08f), Color.Transparent),
                center = Offset(size.width * 0.8f, size.height * 0.8f),
                radius = size.minDimension * (scale * 1.5f)
            ),
            center = Offset(size.width * 0.8f, size.height * 0.8f),
            radius = size.minDimension * (scale * 1.5f)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun StillAuraBackgroundPreview() {
    SafetyNetTheme {
        StillAuraBackground()
    }
}