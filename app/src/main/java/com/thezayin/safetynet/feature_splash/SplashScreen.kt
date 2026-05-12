package com.thezayin.safetynet.feature_splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thezayin.safetynet.core.presentation.util.ObserveEffect
import com.thezayin.safetynet.feature_splash.component.SplashContent
import com.thezayin.safetynet.feature_splash.mvi.SplashEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = koinViewModel(),
    onNavigate: (SplashEffect) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.effect) { effect ->
        onNavigate(effect)
    }

    SplashContent(isLoading = state.isLoading)
}