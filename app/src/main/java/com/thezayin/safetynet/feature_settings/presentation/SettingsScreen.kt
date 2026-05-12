package com.thezayin.safetynet.feature_settings.presentation

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
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val validator: ValidateSettingsUseCase = koinInject()

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
                is SettingsEffect.OpenUrl -> { /* Intent to browser */
                }

                is SettingsEffect.OpenEmailClient -> { /* Intent to email */
                }

                is SettingsEffect.ShowError -> { /* Show Snackbar */
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
                    currentInterval = state.intervalHours, onSave = {
                        viewModel.onIntent(SettingsIntent.UpdateInterval(it))
                        activeSheet = null
                    })

                else -> {}
            }
        }
    }
}

private enum class SettingsSheet { Name, ContactName, ContactEmail, Interval }