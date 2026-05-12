package com.thezayin.safetynet.feature_onboarding.presentation.slides

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.presentation.util.ObserveEffect
import com.thezayin.safetynet.feature_onboarding.presentation.slides.components.SlideUiData
import com.thezayin.safetynet.feature_onboarding.presentation.slides.components.SlidesContent
import com.thezayin.safetynet.feature_onboarding.presentation.slides.mvi.SlideEffect
import com.thezayin.safetynet.feature_onboarding.presentation.slides.mvi.SlideIntent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SlidesScreen(
    onNavigateToProfileSetup: () -> Unit,
    viewModel: SlideViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val pages = remember {
        listOf(
            SlideUiData(R.string.slide_1_title, R.string.slide_1_desc),
            SlideUiData(R.string.slide_2_title, R.string.slide_2_desc),
            SlideUiData(R.string.slide_3_title, R.string.slide_3_desc)
        )
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is SlideEffect.PagerScrollTo -> {
                scope.launch {
                    pagerState.animateScrollToPage(effect.pageIndex)
                }
            }
            is SlideEffect.NavigateToProfileSetup -> {
                onNavigateToProfileSetup()
            }
        }
    }

    SlidesContent(
        pages = pages,
        pagerState = pagerState,
        isLastPage = state.isLastPage,
        onPageChanged = { index ->
            viewModel.onIntent(SlideIntent.OnPageScroll(index))
        },
        onNextClick = {
            viewModel.onIntent(SlideIntent.OnPrimaryActionClicked)
        }
    )
}