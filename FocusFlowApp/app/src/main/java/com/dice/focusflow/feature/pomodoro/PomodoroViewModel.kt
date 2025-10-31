package com.dice.focusflow.feature.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max

class PomodoroViewModel(
    private var config: PomodoroConfig = PomodoroConfig()
) : ViewModel() {

    private val _state = MutableStateFlow(
        PomodoroState(
            phase = PomodoroPhase.FOCUS,
            remainingSeconds = config.totalSecondsFor(PomodoroPhase.FOCUS),
            isRunning = false,
            completedPomodoros = 0,
            cycleCount = 0
        )
    )
    val state: StateFlow<PomodoroState> = _state

    private var tickJob: Job? = null

    fun start() {
        if (_state.value.isRunning) return

        _state.update { it.copy(isRunning = true) }
        ensureTicking()
    }

    fun pause() {
        _state.update { it.copy(isRunning = false) }
        cancelTick()
    }

    fun resume() = start()

    fun resetToFocus() {
        cancelTick()
        _state.update {
            it.copy(
                phase = PomodoroPhase.FOCUS,
                remainingSeconds = config.totalSecondsFor(PomodoroPhase.FOCUS),
                isRunning = false
            )
        }
    }

    fun skipPhase() {

        transitionToNextPhase(finishedFocus = (_state.value.phase == PomodoroPhase.FOCUS))
    }

    fun setConfig(newConfig: PomodoroConfig) {
        config = newConfig

        if (!_state.value.isRunning) {
            _state.update {
                it.copy(
                    remainingSeconds = config.totalSecondsFor(it.phase)
                )
            }
        }
    }

    fun progressFraction(): Float {
        val total = config.totalSecondsFor(_state.value.phase).toFloat()
        if (total <= 0f) return 0f
        return 1f - (_state.value.remainingSeconds / total)
    }

    private fun ensureTicking() {
        if (tickJob?.isActive == true) return
        tickJob = viewModelScope.launch {
            while (isActive && _state.value.isRunning) {
                delay(1000)
                _state.update { curr ->
                    val next = max(0, curr.remainingSeconds - 1)
                    curr.copy(remainingSeconds = next)
                }
                if (_state.value.remainingSeconds == 0) {
                    val finishedFocus = (_state.value.phase == PomodoroPhase.FOCUS)
                    transitionToNextPhase(finishedFocus)
                }
            }
        }
    }

    private fun transitionToNextPhase(finishedFocus: Boolean) {
        _state.update { curr ->
            if (finishedFocus) {
                val newCompleted = curr.completedPomodoros + 1
                val newCycle = curr.cycleCount + 1
                val nextPhase = if (newCycle % config.longBreakEvery == 0)
                    PomodoroPhase.LONG_BREAK else PomodoroPhase.SHORT_BREAK
                curr.copy(
                    phase = nextPhase,
                    remainingSeconds = config.totalSecondsFor(nextPhase),
                    isRunning = true, // segue rodando automaticamente
                    completedPomodoros = newCompleted,
                    cycleCount = newCycle
                )
            } else {

                val nextPhase = PomodoroPhase.FOCUS
                curr.copy(
                    phase = nextPhase,
                    remainingSeconds = config.totalSecondsFor(nextPhase),
                    isRunning = true
                )
            }
        }

        ensureTicking()
    }

    private fun cancelTick() {
        tickJob?.cancel()
        tickJob = null
    }
}
