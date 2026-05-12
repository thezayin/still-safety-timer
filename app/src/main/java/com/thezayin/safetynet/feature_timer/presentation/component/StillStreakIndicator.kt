package com.thezayin.safetynet.feature_timer.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ui.theme.AuraMint
import com.thezayin.safetynet.core.ui.theme.TextCloud
import com.thezayin.safetynet.core.ui.theme.TwilightBackground

@Composable
fun StillStreakIndicator(
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(16.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .blur(6.dp)
                    .background(AuraMint.copy(alpha = 0.4f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(AuraMint, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.streak_days_of_stillness, count),
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 3.sp,
                color = TextCloud.copy(alpha = 0.6f)
            )
        )
    }
}

@Preview(name = "Standard Streak", showBackground = true, backgroundColor = 0xFF151921)
@Composable
fun StillStreakIndicatorPreview() {
    Box(
        modifier = Modifier
            .background(TwilightBackground)
            .padding(8.dp)
    ) {
        StillStreakIndicator(count = 14)
    }
}

@Preview(name = "Zero State", showBackground = true, backgroundColor = 0xFF151921)
@Composable
fun StillStreakIndicatorZeroPreview() {
    Box(
        modifier = Modifier
            .background(TwilightBackground)
            .padding(8.dp)
    ) {
        StillStreakIndicator(count = 0)
    }
}