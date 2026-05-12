package com.thezayin.safetynet.feature_timer.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thezayin.safetynet.core.ui.theme.TextCloud
import com.thezayin.safetynet.core.ui.theme.TwilightBackground

@Composable
fun PhaseStatusBadge(
    status: String, visible: Boolean
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Text(
            text = status,
            modifier = Modifier
                .border(
                    width = 0.5.dp,
                    color = TextCloud.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 8.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 2.sp,
                color = TextCloud.copy(alpha = 0.4f)
            )
        )
    }
}

@Preview(name = "Badge Preview", showBackground = true, backgroundColor = 0xFF151921)
@Composable
fun PhaseStatusBadgePreview() {
    Box(
        modifier = Modifier
            .background(TwilightBackground)
            .padding(16.dp)
    ) {
        PhaseStatusBadge(
            status = "S Y S T E M   A R M E D", visible = true
        )
    }
}