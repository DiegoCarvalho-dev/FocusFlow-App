package com.dice.focusflow.feature.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PomodoroViewModel(
    private var config: PomodoroConfig = PomodoroConfig()
) : ViewModel() {

    private val _state = MutableStateFlow(
        PomodoroState(
            phase = PomodoroPhase.Focus,
            remainingSeconds = config.focusMinutes * 60,
            isRunning = false,
            completedPomodoros = 0
        )
    )
    val state: StateFlow<PomodoroState> = _state

    val progressFraction: StateFlow<Float> = _state.map {
        val totalSeconds = defaultSecondsFor(it.phase)
        if (totalSeconds > 0) {
            1f - (it.remainingSeconds.toFloat() / totalSeconds.toFloat())
        } else {
            0f
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0f
    )

    private var tickerJob: Job? = null

    fun setConfig(newConfig: PomodoroConfig) {
        config = newConfig
        resetToFocus()
    }

    fun start() {
        if (_state.value.isRunning) return
        _state.value = _state.value.copy(isRunning = true)
        startTicker()
    }

    fun pause() {
        if (!_state.value.isRunning) return
        _state.value = _state.value.copy(isRunning = false)
        stopTicker()
    }

    fun resetToFocus() {
        stopTicker()
        _state.value = PomodoroState(
            phase = PomodoroPhase.Focus,
            remainingSeconds = config.focusMinutes * 60,
            isRunning = false,
            completedPomodoros = 0
        )
    }

    fun skipPhase() {
        stopTicker()
        advancePhase(skipped = true)
    }

    private fun startTicker() {
        stopTicker()
        tickerJob = viewModelScope.launch {
            while (_state.value.isRunning) {
                delay(1000)
                tick()
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
    }

    private fun tick() {
        val s = _state.value
        if (!s.isRunning) return

        val next = s.remainingSeconds - 1
        if (next > 0) {
            _state.value = s.copy(remainingSeconds = next)
        } else {
            advancePhase()
        }
    }
    fun reset() {
        stopTicker()
        _state.value = _state.value.copy(
            phase = PomodoroPhase.Focus,
            remainingSeconds = defaultSecondsFor(PomodoroPhase.Focus),
            isRunning = false
        )
    }
    private fun advancePhase(skipped: Boolean = false) {
        val s = _state.value
        when (s.phase) {
            PomodoroPhase.Focus -> {
                val completed = if (skipped) s.completedPomodoros else s.completedPomodoros + 1
                val nextPhase =
                    if (completed % config.cyclesUntilLongBreak == 0) PomodoroPhase.LongBreak
                    else PomodoroPhase.ShortBreak

                _state.value = s.copy(
                    phase = nextPhase,
                    remainingSeconds = defaultSecondsFor(nextPhase),
                    isRunning = false,
                    completedPomodoros = completed
                )
            }
            PomodoroPhase.ShortBreak, PomodoroPhase.LongBreak -> {
                _state.value = s.copy(
                    phase = PomodoroPhase.Focus,
                    remainingSeconds = defaultSecondsFor(PomodoroPhase.Focus),
                    isRunning = false
                )
            }
        }
    }

    private fun defaultSecondsFor(phase: PomodoroPhase): Int = when (phase) {
        PomodoroPhase.Focus -> config.focusMinutes * 60
        PomodoroPhase.ShortBreak -> config.shortBreakMinutes * 60
        PomodoroPhase.LongBreak -> config.longBreakMinutes * 60
    }
}

