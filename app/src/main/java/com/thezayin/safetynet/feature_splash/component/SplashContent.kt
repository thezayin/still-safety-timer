package com.thezayin.safetynet.feature_splash.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ui.theme.AuraMint
import com.thezayin.safetynet.core.ui.theme.SplashBlack
import com.thezayin.safetynet.core.ui.theme.SplashDeepTeal

@Composable
fun SplashContent(isLoading: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "SplashPulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.05f, animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse
        ), label = "LogoScale"
    )

    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.1f, animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Reverse
        ), label = "GlowIntensity"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(SplashDeepTeal, SplashBlack)
                )
            ), contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(animationSpec = tween(1000)),
            exit = fadeOut(animationSpec = tween(600))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(350.dp)
                    .offset(y = (-40).dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    AuraMint.copy(alpha = 0.12f * glowIntensity), Color.Transparent
                                )
                            ), radius = (size.width / 1.8f) * glowIntensity
                        )
                    }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_still_logo),
                    contentDescription = "SafetyNet Logo",
                    modifier = Modifier
                        .size(170.dp)
                        .scale(scale)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Splash Loading State")
@Composable
private fun SplashPreviewLoading() {
    SplashContent(isLoading = true)
}

@Preview(showBackground = true, name = "Splash Exit State")
@Composable
private fun SplashPreviewExit() {
    SplashContent(isLoading = false)
}