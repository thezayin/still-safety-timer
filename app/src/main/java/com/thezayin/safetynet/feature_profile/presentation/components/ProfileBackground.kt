package com.thezayin.safetynet.feature_profile.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.thezayin.safetynet.core.ui.theme.AuraMint
import com.thezayin.safetynet.core.ui.theme.StillAnimation

@Composable
fun ProfileBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "ProfileAura")

    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = StillAnimation.ZenEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuraScale"
    )

    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = StillAnimation.ZenEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuraAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(auraAlpha)
                .blur(80.dp)
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AuraMint, Color.Transparent),
                    center = center.copy(y = center.y * 1.2f),
                    radius = size.width * auraScale
                )
            )
        }
    }
}

