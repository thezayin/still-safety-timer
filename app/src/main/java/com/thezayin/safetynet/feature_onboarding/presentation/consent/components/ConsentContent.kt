package com.thezayin.safetynet.feature_onboarding.presentation.consent.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thezayin.safetynet.core.ui.components.GhostButton
import com.thezayin.safetynet.core.ui.components.PrimaryButton
import com.thezayin.safetynet.core.ui.components.StillAuraBackground
import com.thezayin.safetynet.core.ui.theme.SafetyNetTheme

@Composable
fun ConsentContent(
    title: String,
    description: String,
    bullets: List<String>,
    checkboxLabel: String,
    buttonText: String,
    isAccepted: Boolean,
    isLoading: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onFinishClick: () -> Unit,
    onDeclineClick: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val privacyPolicyUrl =
        "https://safetynet-privacy.blogspot.com/2026/05/privacy-policy-safety-protocols.html"

    Box(modifier = Modifier.fillMaxSize()) {
        StillAuraBackground()

        Scaffold(
            containerColor = Color.Transparent, bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EntranceAnimation(delay = 1000) {
                        PrimaryButton(
                            text = buttonText,
                            onClick = onFinishClick,
                            enabled = isAccepted && !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    EntranceAnimation(delay = 1150) {
                        GhostButton(
                            label = "Decline & Exit",
                            onClick = onDeclineClick,
                            modifier = Modifier.fillMaxWidth(0.7f)
                        )
                    }
                }
            }) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .statusBarsPadding()
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                EntranceAnimation(delay = 0) {
                    Text(text = title, style = MaterialTheme.typography.displayLarge)
                }
                EntranceAnimation(delay = 200) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
                ConsentBulletPoints(points = bullets)
                Spacer(modifier = Modifier.weight(1f))
                EntranceAnimation(delay = 800) {
                    Column {
                        ConsentCheckbox(
                            checked = isAccepted,
                            onCheckedChange = onCheckedChange,
                            label = checkboxLabel
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Privacy Policy & Safety Protocols",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge.copy(textDecoration = TextDecoration.Underline),
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .clickable { uriHandler.openUri(privacyPolicyUrl) })
                    }
                }
                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }
}

@Preview(showSystemUi = true, name = "Consent - Unaccepted State")
@Composable
private fun ConsentContentUnacceptedPreview() {
    SafetyNetTheme {
        ConsentContent(
            title = "THE SAFETY\nCONTRACT",
            description = "Stillness is a commitment to your own safety. Please acknowledge our shared protocols.",
            bullets = listOf(
                "Encrypted and private location data.",
                "Minimalist check-in prompts.",
                "Secure emergency notifications."
            ),
            checkboxLabel = "I understand and accept these protocols.",
            buttonText = "ENTER THE STILLNESS",
            isAccepted = false,
            isLoading = false,
            onCheckedChange = {},
            onFinishClick = {},
            onDeclineClick = {})
    }
}

@Preview(showSystemUi = true, name = "Consent - Accepted State")
@Composable
private fun ConsentContentAcceptedPreview() {
    SafetyNetTheme {
        ConsentContent(
            title = "THE SAFETY\nCONTRACT",
            description = "Stillness is a commitment to your own safety. Please acknowledge our shared protocols.",
            bullets = listOf(
                "Encrypted and private location data.",
                "Minimalist check-in prompts.",
                "Secure emergency notifications."
            ),
            checkboxLabel = "I understand and accept these protocols.",
            buttonText = "ENTER THE STILLNESS",
            isAccepted = true,
            isLoading = false,
            onCheckedChange = {},
            onFinishClick = {},
            onDeclineClick = {})
    }
}