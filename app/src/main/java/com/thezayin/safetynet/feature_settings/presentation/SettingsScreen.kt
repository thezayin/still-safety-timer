package com.thezayin.safetynet.feature_settings.presentation

import android.app.Activity
import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.thezayin.safetynet.core.ads.manager.AdManager
import com.thezayin.safetynet.core.ads.model.AdType
import com.thezayin.safetynet.feature_settings.domain.usecase.ValidateSettingsUseCase
import com.thezayin.safetynet.feature_settings.presentation.components.EditFieldBottomSheet
import com.thezayin.safetynet.feature_settings.presentation.components.IntervalSelectorSheet
import com.thezayin.safetynet.feature_settings.presentation.components.SettingsContent
import com.thezayin.safetynet.feature_settings.presentation.mvi.SettingsEffect
import com.thezayin.safetynet.feature_settings.presentation.mvi.SettingsIntent
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSplash: () -> Unit,
    adManager: AdManager = koinInject(),
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val validator: ValidateSettingsUseCase = koinInject()
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        adManager.load(AdType.NATIVE_HOME)
        adManager.load(AdType.REWARDED)
    }

    var activeSheet by remember { mutableStateOf<SettingsSheet?>(null) }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.onIntent(SettingsIntent.RefreshPermissionStatus)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.NavigateToSplash -> onNavigateToSplash()
                is SettingsEffect.OpenUrl -> {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse(effect.url)
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "No browser found to open this link.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is SettingsEffect.OpenEmailClient -> {
                    val intent =
                        android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                            data = android.net.Uri.parse("mailto:${effect.address}")
                            putExtra(android.content.Intent.EXTRA_SUBJECT, effect.subject)
                        }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "No email app found.", Toast.LENGTH_SHORT).show()
                    }
                }

                is SettingsEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    SettingsContent(
        userName = state.userName,
        contactName = state.contactName,
        contactEmail = state.contactEmail,
        interval = state.intervalHours,
        isNotificationsReady = state.isNotificationsReady,
        isAlarmsReady = state.isExactAlarmReady,
        onBackClick = onNavigateBack,
        onEditName = { activeSheet = SettingsSheet.Name },
        onEditContactName = { activeSheet = SettingsSheet.ContactName },
        onEditContactEmail = { activeSheet = SettingsSheet.ContactEmail },
        onChangeInterval = { activeSheet = SettingsSheet.Interval },
        onFixNotification = { viewModel.onIntent(SettingsIntent.RefreshPermissionStatus) },
        onFixAlarms = { viewModel.onIntent(SettingsIntent.RefreshPermissionStatus) },
        onPrivacyClick = { viewModel.onIntent(SettingsIntent.OpenPrivacyPolicy) },
        onTermsClick = { viewModel.onIntent(SettingsIntent.OpenTermsOfService) },
        onContactUs = { viewModel.onIntent(SettingsIntent.ContactSupport) },
        onWipeData = { viewModel.onIntent(SettingsIntent.WipeAllData) })

    if (activeSheet != null) {
        ModalBottomSheet(
            onDismissRequest = { activeSheet = null }, sheetState = sheetState
        ) {
            when (activeSheet) {
                SettingsSheet.Name -> EditFieldBottomSheet(
                    title = "Legal Name",
                    initialValue = state.userName,
                    validate = { validator.validateName(it) == null },
                    onCancel = { activeSheet = null },
                    onSave = {
                        viewModel.onIntent(SettingsIntent.UpdateUserName(it))
                        activeSheet = null
                    })

                SettingsSheet.ContactName -> EditFieldBottomSheet(
                    title = "Guardian Name",
                    initialValue = state.contactName,
                    validate = { validator.validateName(it) == null },
                    onCancel = { activeSheet = null },
                    onSave = {
                        viewModel.onIntent(SettingsIntent.UpdateContactName(it))
                        activeSheet = null
                    })

                SettingsSheet.ContactEmail -> EditFieldBottomSheet(
                    title = "Guardian Email",
                    initialValue = state.contactEmail,
                    validate = { validator.validateEmail(it) == null },
                    onCancel = { activeSheet = null },
                    onSave = {
                        viewModel.onIntent(SettingsIntent.UpdateContactEmail(it))
                        activeSheet = null
                    })

                SettingsSheet.Interval -> IntervalSelectorSheet(
                    currentInterval = state.intervalHours, onSave = { hours ->
                        if (hours == 48) {
                            if (activity != null) {
                                adManager.show(AdType.REWARDED, activity) { success ->
                                    if (success) {
                                        viewModel.onIntent(SettingsIntent.UpdateInterval(48))
                                        activeSheet = null
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Ad skipped, cannot unlock 48h.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            viewModel.onIntent(SettingsIntent.UpdateInterval(24))
                            activeSheet = null
                        }
                    })

                else -> {}
            }
        }
    }
}

private enum class SettingsSheet { Name, ContactName, ContactEmail, Interval }