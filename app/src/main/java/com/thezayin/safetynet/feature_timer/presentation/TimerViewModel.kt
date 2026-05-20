package com.thezayin.safetynet.feature_timer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.monitor.PowerMonitor
import com.thezayin.safetynet.core.domain.utils.TimeFormatter
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import com.thezayin.safetynet.feature_timer.domain.usecase.AbortEmergencyUseCase
import com.thezayin.safetynet.feature_timer.domain.usecase.CheckInUseCase
import com.thezayin.safetynet.feature_timer.domain.usecase.ObserveTimerPhaseUseCase
import com.thezayin.safetynet.feature_timer.domain.usecase.StartTimerUseCase
import com.thezayin.safetynet.feature_timer.domain.usecase.StopTimerUseCase
import com.thezayin.safetynet.feature_timer.domain.usecase.UpdateTimerDurationUseCase
import com.thezayin.safetynet.feature_timer.presentation.contract.TimerEffect
import com.thezayin.safetynet.feature_timer.presentation.contract.TimerIntent
import com.thezayin.safetynet.feature_timer.presentation.contract.TimerState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerViewModel(
    private val repository: TimerRepository,
    private val startTimerUseCase: StartTimerUseCase,
    private val stopTimerUseCase: StopTimerUseCase,
    private val checkInUseCase: CheckInUseCase,
    private val abortEmergencyUseCase: AbortEmergencyUseCase,
    private val updateDurationUseCase: UpdateTimerDurationUseCase,
    private val observeTimerPhase: ObserveTimerPhaseUseCase,
    private val powerMonitor: PowerMonitor
) : ViewModel() {

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private val _effect = Channel<TimerEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        observeTimerLogic()
        observePersistentData()
        observeBatterySaver()
        refreshBatteryWarningState()
    }

    fun onIntent(intent: TimerIntent) {
        when (intent) {
            is TimerIntent.StartTimer -> {
                handleAction {
                    startTimerUseCase(bypassBatteryCheck = intent.ignoreBatteryWarning)
                }
            }

            TimerIntent.StopTimer -> handleAction { stopTimerUseCase() }
            TimerIntent.CheckIn -> handleAction { checkInUseCase() }
            TimerIntent.AbortEmergency -> handleAction { abortEmergencyUseCase() }
            is TimerIntent.UpdateDuration -> handleAction { updateDurationUseCase(intent.hours) }
            TimerIntent.OnSettingsClicked -> {
                viewModelScope.launch { _effect.send(TimerEffect.NavigateToSettings) }
            }
            TimerIntent.RefreshWarningState -> refreshBatteryWarningState()
        }
    }

    private fun handleAction(action: suspend () -> DomainResult<Unit>) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = action()) {
                is DomainResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(TimerEffect.TriggerSuccessHaptic)
                    refreshBatteryWarningState()
                }

                is DomainResult.Failure -> {
                    _state.update { it.copy(isLoading = false) }
                    handleFailure(result.error)
                }
            }
        }
    }

    private suspend fun handleFailure(error: AppError) {
        when (error) {
            is AppError.Permission.ExactAlarmDenied -> {
                _effect.send(TimerEffect.OpenExactAlarmSettings)
            }

            is AppError.System.PowerSaverActive -> {
                if (!powerMonitor.isIgnoringBatteryOptimizations()) {
                    _effect.send(TimerEffect.TriggerBatteryExemptionPopup)
                } else {
                    _effect.send(TimerEffect.ShowError("Please disable Battery Saver to ensure SOS reliability."))
                }
                refreshBatteryWarningState()
            }

            else -> {
                _effect.send(TimerEffect.ShowError("System error. Please try again."))
            }
        }
    }

    private fun observeBatterySaver() {
        powerMonitor.batterySaverState
            .onEach { isEnabled ->
                _state.update { it.copy(isBatterySaverAlertVisible = isEnabled) }
            }.launchIn(viewModelScope)
    }

    private fun refreshBatteryWarningState() {
        val isRestricted = !powerMonitor.isIgnoringBatteryOptimizations()
        val isBatterySaverOn = powerMonitor.isBatterySaverEnabled()
        _state.update {
            it.copy(
                isBatteryWarningActive = isRestricted,
                isBatterySaverAlertVisible = isBatterySaverOn
            )
        }
    }
    private fun observeTimerLogic() {
        observeTimerPhase()
            .onEach { newPhase ->
                _state.update {
                    it.copy(
                        phase = newPhase,
                        displayTime = TimeFormatter.formatPhase(newPhase)
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun observePersistentData() {
        repository.timerData
            .onEach { data ->
                _state.update {
                    it.copy(
                        durationHours = data.durationHours,
                        streakCount = data.streak
                    )
                }
            }.launchIn(viewModelScope)
    }
}