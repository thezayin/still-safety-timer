package com.thezayin.safetynet.feature_timer.presentation.component

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ui.theme.*
import com.thezayin.safetynet.feature_timer.domain.model.TimerPhase

@Composable
fun StillAuraOrb(
    phase: TimerPhase,
    displayTime: String,
    modifier: Modifier = Modifier
) {
    val targetColor = when (phase) {
        is TimerPhase.Active -> AuraMint
        is TimerPhase.Warning -> AuraSunset
        is TimerPhase.Critical, is TimerPhase.Abort, is TimerPhase.Expired -> AuraCoral
        else -> TextCloud.copy(alpha = 0.1f)
    }

    val animatedColor by animateColorAsState(targetValue = targetColor, animationSpec = tween(2000), label = "")

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (phase is TimerPhase.Critical || phase is TimerPhase.Abort) 1000 else 3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(340.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(animatedColor.copy(alpha = 0.2f * pulseScale), Color.Transparent),
                    center = center,
                    radius = size.minDimension / 1.1f
                )
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = displayTime,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = if (phase is TimerPhase.Abort) 82.sp else 58.sp,
                    fontWeight = FontWeight.ExtraLight,
                    letterSpacing = 2.sp,
                    color = TextCloud
                )
            )
            Text(
                text = if (phase is TimerPhase.Abort) stringResource(R.string.orb_confirm_safety) else stringResource(R.string.orb_remaining),
                style = MaterialTheme.typography.labelSmall,
                color = animatedColor.copy(alpha = 0.5f),
                letterSpacing = 5.sp
            )
        }
    }
}

@Preview
@Composable
fun StillAuraOrbPreview() {
    StillAuraOrb(phase = TimerPhase.Active(3600000), displayTime = "01:00:00")
}