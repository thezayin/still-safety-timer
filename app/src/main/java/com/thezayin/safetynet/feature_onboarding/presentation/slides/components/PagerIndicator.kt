package com.thezayin.safetynet.feature_onboarding.presentation.slides.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thezayin.safetynet.core.ui.theme.SafetyNetTheme
import com.thezayin.safetynet.core.ui.theme.StillAnimation

@Composable
fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage

            val width by animateDpAsState(
                targetValue = if (isActive) 24.dp else 8.dp,
                animationSpec = StillAnimation.stillTween(StillAnimation.SnappyDuration),
                label = "IndicatorWidth"
            )

            val color by animateColorAsState(
                targetValue = if (isActive) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.2f),
                animationSpec = StillAnimation.stillTween(StillAnimation.SnappyDuration),
                label = "IndicatorColor"
            )

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Preview
@Composable
private fun PagerIndicatorPreview() {
    SafetyNetTheme {
        Surface(color = Color(0xFF151921)) {
            Column(modifier = Modifier.padding(20.dp)) {
                PagerIndicator(pageCount = 3, currentPage = 1)
            }
        }
    }
}