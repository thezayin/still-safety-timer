package com.thezayin.safetynet.feature_onboarding.presentation.slides.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thezayin.safetynet.core.ui.components.PrimaryButton
import com.thezayin.safetynet.core.ui.theme.SafetyNetTheme
import com.thezayin.safetynet.R

@Composable
fun OnboardingFooter(
    pageCount: Int,
    currentPage: Int,
    isLastPage: Boolean,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PagerIndicator(
            pageCount = pageCount,
            currentPage = currentPage,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        PrimaryButton(
            text = if (isLastPage) {
                stringResource(R.string.slide_button_get_started)
            } else {
                stringResource(R.string.slide_button_next)
            },
            onClick = onNextClick
        )
    }
}

@Preview
@Composable
private fun OnboardingFooterPreview() {
    SafetyNetTheme {
        Surface(color = Color(0xFF151921)) {
            OnboardingFooter(
                pageCount = 3,
                currentPage = 0,
                isLastPage = false,
                onNextClick = {}
            )
        }
    }
}