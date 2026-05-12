package com.thezayin.safetynet.feature_onboarding.presentation.consent.components

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.thezayin.safetynet.core.ui.theme.StillAnimation

@Composable
fun EntranceAnimation(
    delay: Int = 0,
    content: @Composable () -> Unit
) {
    val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }
    val transition = rememberTransition(visibleState, label = "EntranceTransition")

    val alpha by transition.animateFloat(
        transitionSpec = { StillAnimation.stillTween(duration = 1000, delay = delay) },
        label = "Alpha"
    ) { if (it) 1f else 0f }

    val translateY by transition.animateFloat(
        transitionSpec = { StillAnimation.stillTween(duration = 1200, delay = delay) },
        label = "TranslationY"
    ) { if (it) 0f else 40f }

    Box(
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha
            this.translationY = translateY
        }
    ) {
        content()
    }
}