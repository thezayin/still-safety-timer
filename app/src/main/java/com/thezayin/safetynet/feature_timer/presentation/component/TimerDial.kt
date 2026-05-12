package com.thezayin.safetynet.feature_timer.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thezayin.safetynet.core.domain.utils.TimeFormatter
import com.thezayin.safetynet.core.ui.theme.AuraCoral
import com.thezayin.safetynet.core.ui.theme.AuraMint
import com.thezayin.safetynet.core.ui.theme.AuraSunset
import com.thezayin.safetynet.core.ui.theme.GlassBorder
import com.thezayin.safetynet.core.ui.theme.TextCloud
import com.thezayin.safetynet.feature_timer.domain.model.TimerPhase

@Composable
fun TimerDial(
    phase: TimerPhase,
    durationHours: Int,
    modifier: Modifier = Modifier
) {
    val remainingMillis = when (phase) {
        is TimerPhase.Active -> phase.remainingMillis
        is TimerPhase.Warning -> phase.remainingMillis
        is TimerPhase.Critical -> phase.remainingMillis
        else -> 0L
    }

    val targetProgress = if (phase is TimerPhase.Idle) 1f
    else TimeFormatter.calculateProgress(remainingMillis, durationHours)

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        label = "DialProgress"
    )

    val targetColor = when (phase) {
        is TimerPhase.Idle, is TimerPhase.Active -> AuraMint
        is TimerPhase.Warning -> AuraSunset
        is TimerPhase.Critical, is TimerPhase.Abort, is TimerPhase.Expired -> AuraCoral
    }
    val animatedColor by animateColorAsState(targetValue = targetColor, label = "DialColor")

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(280.dp)) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = GlassBorder,
            strokeWidth = 8.dp,
            strokeCap = StrokeCap.Round
        )
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxSize(),
            color = animatedColor,
            strokeWidth = 8.dp,
            strokeCap = StrokeCap.Round
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (phase is TimerPhase.Idle) "${durationHours}h" else TimeFormatter.formatPhase(
                    phase
                ),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = TextCloud
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF151921)
@Composable
fun TimerDialPreview() {
    Box(Modifier.padding(20.dp)) {
        TimerDial(phase = TimerPhase.Active(3600000L), durationHours = 2)
    }
}