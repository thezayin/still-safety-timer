package com.thezayin.safetynet.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.thezayin.safetynet.feature_onboarding.presentation.consent.ConsentScreen
import com.thezayin.safetynet.feature_onboarding.presentation.permissions.PermissionScreen
import com.thezayin.safetynet.feature_onboarding.presentation.slides.SlidesScreen
import com.thezayin.safetynet.feature_profile.presentation.ContactEmailScreen
import com.thezayin.safetynet.feature_profile.presentation.ContactNameScreen
import com.thezayin.safetynet.feature_profile.presentation.NameSetupScreen
import com.thezayin.safetynet.feature_profile.presentation.ProfileReviewScreen
import com.thezayin.safetynet.feature_profile.presentation.ProfileViewModel
import com.thezayin.safetynet.feature_settings.presentation.SettingsScreen
import com.thezayin.safetynet.feature_splash.SplashScreen
import com.thezayin.safetynet.feature_splash.mvi.SplashEffect
import com.thezayin.safetynet.feature_timer.presentation.TimerScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun StillNavGraph(navController: NavHostController) {
    androidx.compose.material3.Surface(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        color = com.thezayin.safetynet.core.ui.theme.TwilightBackground // Use your dark background color
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash,
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(400)) }) {
            composable<Screen.Splash>(
                exitTransition = {
                    scaleOut(animationSpec = tween(700)) + fadeOut(animationSpec = tween(500))
                }) {
                SplashScreen(
                    viewModel = koinViewModel(), onNavigate = { effect ->
                        when (effect) {
                            SplashEffect.NavigateToOnboarding -> {
                                navController.navigate(Screen.Consent) {
                                    popUpTo(Screen.Splash) { inclusive = true }
                                }
                            }

                            SplashEffect.NavigateToProfileSetup -> {
                                navController.navigate(Screen.ProfileGraph) {
                                    popUpTo(Screen.Splash) { inclusive = true }
                                }
                            }

                            SplashEffect.NavigateToDashboard -> {
                                navController.navigate(Screen.Timer) {
                                    popUpTo(Screen.Splash) { inclusive = true }
                                }
                            }
                        }
                    })
            }

            composable<Screen.Consent> {
                ConsentScreen(
                    onNavigateToPermissions = { navController.navigate(Screen.PermissionSetup) })
            }

            composable<Screen.PermissionSetup> {
                PermissionScreen(
                    onNavigateNext = { navController.navigate(Screen.Onboarding) })
            }

            composable<Screen.Onboarding> {
                SlidesScreen(
                    onNavigateToProfileSetup = { navController.navigate(Screen.ProfileGraph) })
            }

            navigation<Screen.ProfileGraph>(
                startDestination = Screen.EnterName
            ) {
                composable<Screen.EnterName> { entry ->
                    val parentEntry =
                        remember(entry) { navController.getBackStackEntry(Screen.ProfileGraph) }
                    val sharedViewModel =
                        koinViewModel<ProfileViewModel>(viewModelStoreOwner = parentEntry)

                    NameSetupScreen(
                        viewModel = sharedViewModel,
                        onNavigateNext = { navController.navigate(Screen.EnterContactName) },
                        onNavigateBack = { navController.popBackStack() })
                }

                composable<Screen.EnterContactName> { entry ->
                    val parentEntry =
                        remember(entry) { navController.getBackStackEntry(Screen.ProfileGraph) }
                    val sharedViewModel =
                        koinViewModel<ProfileViewModel>(viewModelStoreOwner = parentEntry)

                    ContactNameScreen(
                        viewModel = sharedViewModel,
                        onNavigateNext = { navController.navigate(Screen.EnterContactEmail) },
                        onNavigateBack = { navController.popBackStack() })
                }

                composable<Screen.EnterContactEmail> { entry ->
                    val parentEntry =
                        remember(entry) { navController.getBackStackEntry(Screen.ProfileGraph) }
                    val sharedViewModel =
                        koinViewModel<ProfileViewModel>(viewModelStoreOwner = parentEntry)

                    ContactEmailScreen(
                        viewModel = sharedViewModel,
                        onNavigateNext = { navController.navigate(Screen.ReviewProfile) },
                        onNavigateBack = { navController.popBackStack() })
                }

                composable<Screen.ReviewProfile> { entry ->
                    val parentEntry =
                        remember(entry) { navController.getBackStackEntry(Screen.ProfileGraph) }
                    val sharedViewModel =
                        koinViewModel<ProfileViewModel>(viewModelStoreOwner = parentEntry)

                    ProfileReviewScreen(viewModel = sharedViewModel, onNavigateNext = {
                        navController.navigate(Screen.Timer) {
                            popUpTo(Screen.Splash) { inclusive = true }
                        }
                    }, onNavigateBack = { navController.popBackStack() })
                }
            }

            composable<Screen.Timer> {
                TimerScreen(
                    onNavigateToSettings = { navController.navigate(Screen.Settings) })
            }

            composable<Screen.Settings> {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSplash = {
                        navController.navigate(Screen.Splash) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}