package com.thezayin.safetynet.feature_onboarding.presentation.consent

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thezayin.safetynet.MainActivity
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ads.manager.AdManager
import com.thezayin.safetynet.core.ads.model.AdType
import com.thezayin.safetynet.core.ads.native_ad.NativeAdComposable // 🔴 Changed Import
import com.thezayin.safetynet.core.presentation.util.ObserveEffect
import com.thezayin.safetynet.feature_onboarding.presentation.consent.components.ConsentContent
import com.thezayin.safetynet.feature_onboarding.presentation.consent.mvi.ConsentEffect
import com.thezayin.safetynet.feature_onboarding.presentation.consent.mvi.ConsentIntent
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ConsentScreen(
    onNavigateToPermissions: () -> Unit,
    viewModel: ConsentViewModel = koinViewModel(),
    adManager: AdManager = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        adManager.load(AdType.NATIVE_HOME)
    }

    BackHandler {
        viewModel.onIntent(ConsentIntent.OnDeclineClicked)
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is ConsentEffect.NavigateToPermissions -> {
                onNavigateToPermissions()
            }

            is ConsentEffect.ShowError -> {
                Toast.makeText(context, "Error: ${effect.error}", Toast.LENGTH_LONG).show()
            }

            is ConsentEffect.ExitApp -> {
                Toast.makeText(context, "Consent required to use SafetyNet.", Toast.LENGTH_SHORT)
                    .show()
                activity?.finish()
            }
        }
    }

    ConsentContent(
        title = stringResource(id = R.string.consent_title),
        description = stringResource(id = R.string.consent_description),
        bullets = listOf(
            stringResource(id = R.string.consent_bullet_location),
            stringResource(id = R.string.consent_bullet_prompts),
            stringResource(id = R.string.consent_bullet_notifications)
        ),
        checkboxLabel = stringResource(id = R.string.consent_checkbox_label),
        buttonText = if (state.isSubmitting) {
            stringResource(id = R.string.consent_button_saving)
        } else {
            stringResource(id = R.string.consent_button_enter)
        },
        isAccepted = state.isAccepted,
        isLoading = state.isSubmitting,
        onCheckedChange = { isChecked ->
            viewModel.onIntent(ConsentIntent.OnToggleAcceptance(isChecked))
        },
        onFinishClick = {
            viewModel.onIntent(ConsentIntent.OnSubmitClicked)
        },
        onDeclineClick = {
            viewModel.onIntent(ConsentIntent.OnDeclineClicked)
        },
        nativeAdContent = {
            NativeAdComposable(
                adType = AdType.NATIVE_HOME,
                adManager = adManager
            )
        }
    )
}