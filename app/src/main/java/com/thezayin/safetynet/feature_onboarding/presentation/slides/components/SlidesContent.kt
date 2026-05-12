package com.thezayin.safetynet.feature_onboarding.presentation.slides.components

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ui.components.StillAuraBackground
import com.thezayin.safetynet.core.ui.theme.SafetyNetTheme

data class SlideUiData(
    @StringRes val titleRes: Int, @StringRes val descriptionRes: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SlidesContent(
    pages: List<SlideUiData>,
    pagerState: PagerState,
    isLastPage: Boolean,
    onPageChanged: (Int) -> Unit,
    onNextClick: () -> Unit
) {
    LaunchedEffect(pagerState.currentPage) {
        onPageChanged(pagerState.currentPage)
    }

    SafetyNetTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            StillAuraBackground()

            Scaffold(
                containerColor = Color.Transparent, bottomBar = {
                    OnboardingFooter(
                        pageCount = pages.size,
                        currentPage = pagerState.currentPage,
                        isLastPage = isLastPage,
                        onNextClick = onNextClick
                    )
                }) { padding ->
                OnboardingPager(
                    pages = pages, pagerState = pagerState, modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showSystemUi = true)
@Composable
private fun SlidesContentLuxuryPreview() {
    val dummyPages = listOf(
        SlideUiData(R.string.slide_1_title, R.string.slide_1_desc),
        SlideUiData(R.string.slide_2_title, R.string.slide_2_desc)
    )

    val pagerState = rememberPagerState(pageCount = { dummyPages.size })

    SlidesContent(
        pages = dummyPages,
        pagerState = pagerState,
        isLastPage = false,
        onPageChanged = {},
        onNextClick = {})
}