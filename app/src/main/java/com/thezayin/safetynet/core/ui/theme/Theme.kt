package com.thezayin.safetynet.core.ui.theme

import android.app.Activity
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TwilightColorScheme = darkColorScheme(
    primary = AuraMint,
    onPrimary = TwilightBackground,
    background = TwilightBackground,
    surface = TwilightSurface,
    onBackground = TextCloud,
    onSurface = TextCloud,
    error = AuraCoral,
    outlineVariant = GlassBorder
)

@OptIn(ExperimentalMaterial3Api::class)
private val LuxuryRippleConfiguration = RippleConfiguration(
    color = TextCloud,
    rippleAlpha = RippleAlpha(
        pressedAlpha = 0.06f,
        focusedAlpha = 0.06f,
        draggedAlpha = 0.08f,
        hoveredAlpha = 0.04f
    )
)

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyNetTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            WindowCompat.setDecorFitsSystemWindows(window, false)

            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = TwilightColorScheme,
        typography = AuraTypography,
    ) {
        CompositionLocalProvider(
            LocalRippleConfiguration provides LuxuryRippleConfiguration,
            content = content
        )
    }
}