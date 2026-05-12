package com.thezayin.safetynet.core.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween

@Suppress("ConstPropertyName")
object StillAnimation {
    const val SnappyDuration = 300
    const val ShortDuration = 500
    const val MediumDuration = 800
    const val LongDuration = 1200

    val ZenEasing = CubicBezierEasing(0.33f, 0f, 0.1f, 1f)


    fun <T> stillTween(
        duration: Int = MediumDuration,
        delay: Int = 0
    ): TweenSpec<T> = tween(
        durationMillis = duration,
        delayMillis = delay,
        easing = ZenEasing
    )

}