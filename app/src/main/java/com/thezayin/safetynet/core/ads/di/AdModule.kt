package com.thezayin.safetynet.core.ads.di

import com.thezayin.safetynet.core.ads.appopen.AppOpenAdHandler
import com.thezayin.safetynet.core.ads.banner.BannerAdHandler
import com.thezayin.safetynet.core.ads.config.AdConfig
import com.thezayin.safetynet.core.ads.interstitial.InterstitialAdHandler
import com.thezayin.safetynet.core.ads.manager.AdManager
import com.thezayin.safetynet.core.ads.model.AdType
import com.thezayin.safetynet.core.ads.native_ad.NativeAdLoader
import com.thezayin.safetynet.core.ads.rewarded.RewardedAdHandler
import com.thezayin.safetynet.core.ads.state.AdState
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val adModule = module {

    // ── Handlers ──────────────────────────────────────────────────────────────
    // Each handler receives a typed onStateUpdate lambda. The lambda captures
    // get<AdManager>() lazily (it runs at call-time, not at construction-time),
    // so there is no circular dependency issue at Koin startup.
    //
    // Retry attempt tracking: when a handler reports Failed, the AdManager
    // schedules a retry. On that retry it calls handler.load() again. When
    // that load fails, the handler calls onStateUpdate(Failed) again. We read
    // the *current* attempt from AdManager's state before emitting the new one.

    single {
        InterstitialAdHandler(
            context = androidContext(),
            onStateUpdate = { state ->
                val adManager = get<AdManager>()
                val nextState = if (state is AdState.Failed) {
                    val currentAttempt =
                        (adManager.observeState(AdType.INTERSTITIAL).value as? AdState.Failed)
                            ?.attempt ?: 0
                    // 🔴 ANALYTICS FIX: Use .copy() to increment attempt but preserve error.code and stage
                    state.copy(attempt = currentAttempt + 1)
                } else state
                adManager.updateState(AdType.INTERSTITIAL, nextState)
            }
        )
    }

    single {
        NativeAdLoader(
            context = androidContext(),
            adUnitId = AdConfig.nativeId,
            onStateUpdate = { state ->
                val adManager = get<AdManager>()
                val nextState = if (state is AdState.Failed) {
                    val currentAttempt =
                        (adManager.observeState(AdType.NATIVE_HOME).value as? AdState.Failed)
                            ?.attempt ?: 0
                    // 🔴 ANALYTICS FIX
                    state.copy(attempt = currentAttempt + 1)
                } else state
                adManager.updateState(AdType.NATIVE_HOME, nextState)
            }
        )
    }

    single {
        AppOpenAdHandler(
            context = androidContext(),
            onStateUpdate = { state ->
                val adManager = get<AdManager>()
                val nextState = if (state is AdState.Failed) {
                    val currentAttempt =
                        (adManager.observeState(AdType.APP_OPEN).value as? AdState.Failed)
                            ?.attempt ?: 0
                    // 🔴 ANALYTICS FIX
                    state.copy(attempt = currentAttempt + 1)
                } else state
                adManager.updateState(AdType.APP_OPEN, nextState)
            }
        )
    }

    single {
        RewardedAdHandler(
            context = androidContext(),
            onStateUpdate = { state ->
                val adManager = get<AdManager>()
                val nextState = if (state is AdState.Failed) {
                    val currentAttempt =
                        (adManager.observeState(AdType.REWARDED).value as? AdState.Failed)
                            ?.attempt ?: 0
                    // 🔴 ANALYTICS FIX
                    state.copy(attempt = currentAttempt + 1)
                } else state
                adManager.updateState(AdType.REWARDED, nextState)
            }
        )
    }

    single {
        BannerAdHandler(
            context = androidContext(),
            // Pass a different AdSize here if the placement needs it, e.g.:
            //   adSize = AdSize.MEDIUM_RECTANGLE
            onStateUpdate = { state ->
                val adManager = get<AdManager>()
                val nextState = if (state is AdState.Failed) {
                    val currentAttempt =
                        (adManager.observeState(AdType.BANNER).value as? AdState.Failed)
                            ?.attempt ?: 0
                    // 🔴 ANALYTICS FIX
                    state.copy(attempt = currentAttempt + 1)
                } else state
                adManager.updateState(AdType.BANNER, nextState)
            }
        )
    }

    // ── AdManager ─────────────────────────────────────────────────────────────
    // Registered AFTER handlers so get<XHandler>() resolves without circularity.
    // AdManager is a singleton — one instance for the entire app process.
    single {
        AdManager(
            handlers = mapOf(
                AdType.INTERSTITIAL to get<InterstitialAdHandler>(),
                AdType.NATIVE_HOME to get<NativeAdLoader>(),
                AdType.APP_OPEN to get<AppOpenAdHandler>(),
                AdType.REWARDED to get<RewardedAdHandler>(),
                AdType.BANNER to get<BannerAdHandler>(),
            ),
            // 🔴 INJECTION FIX: Feed the Brain its newly required dependencies
            analyticsManager = get(),
            remoteConfigManager = get()
        )
    }
}