package com.thezayin.safetynet.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thezayin.safetynet.core.ui.theme.SafetyNetTheme
import com.thezayin.safetynet.core.ui.theme.StillAnimation
import com.thezayin.safetynet.core.ui.theme.TextSlate

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val duration = StillAnimation.SnappyDuration
    val easing = StillAnimation.stillTween<Color>(duration)

    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.97f else 1f,
        animationSpec = StillAnimation.stillTween(duration),
        label = "ButtonScale"
    )

    val containerColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
            isPressed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            else -> MaterialTheme.colorScheme.primary
        }, animationSpec = easing, label = "ContainerColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (enabled) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            TextSlate.copy(alpha = 0.4f)
        }, animationSpec = easing, label = "ContentColor"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .scale(buttonScale)
            .semantics { role = Role.Button },
        shape = RoundedCornerShape(100.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
            disabledContentColor = TextSlate.copy(alpha = 0.4f)
        ),
        elevation = null
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = contentColor
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF151921)
@Composable
private fun PrimaryButtonFixPreview() {
    SafetyNetTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            PrimaryButton(
                text = "Enabled (Contrast Check)", enabled = true, onClick = {})
            PrimaryButton(
                text = "Disabled (Muted Check)", enabled = false, onClick = {})
        }
    }
}