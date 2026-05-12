package com.thezayin.safetynet.feature_timer.domain.usecase

import com.thezayin.safetynet.core.domain.time.TimeProvider
import com.thezayin.safetynet.feature_timer.domain.model.TimerPhase
import com.thezayin.safetynet.feature_timer.domain.repository.TimerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.seconds

class ObserveTimerPhaseUseCase(
    private val repository: TimerRepository,
    private val timeProvider: TimeProvider
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<TimerPhase> = repository.timerData
        .flatMapLatest { data ->
            flow {
                if (!data.isActive) {
                    emit(TimerPhase.Idle)
                    return@flow
                }

                while (true) {
                    val now = timeProvider.currentTimeMillis()
                    val remaining = data.targetTimestamp - now
                    val currentPhase = when {
                        remaining <= 0 -> {
                            val elapsedSinceExpiry = -remaining
                            val abortSecondsRemaining = 60 - (elapsedSinceExpiry / 1000).toInt()

                            if (abortSecondsRemaining > 0) {
                                TimerPhase.Abort(abortSecondsRemaining)
                            } else {
                                TimerPhase.Expired(elapsedSinceExpiry)
                            }
                        }

                        remaining <= 1800000L -> TimerPhase.Critical(remaining)
                        remaining <= 7200000L -> TimerPhase.Warning(remaining)
                        else -> TimerPhase.Active(remaining)
                    }

                    emit(currentPhase)
                    if (currentPhase is TimerPhase.Expired) break
                    delay(1.seconds)
                }
            }
        }
        .distinctUntilChanged()
}