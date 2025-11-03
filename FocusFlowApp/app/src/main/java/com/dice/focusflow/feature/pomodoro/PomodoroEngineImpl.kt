package com.dice.focusflow.feature.pomodoro

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PomodoroEngineImpl(
    private var config: PomodoroConfig = PomodoroConfig()
) : PomodoroEngine {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var tickerJob: Job? = null

    private val _state = MutableStateFlow(
        PomodoroState(
            phase = PomodoroPhase.Focus,
            remainingSeconds = config.focusMinutes * 60,
            isRunning = false,
            completedPomodoros = 0
        )
    )
    override val state: StateFlow<PomodoroState> = _state

    override fun setConfig(newConfig: PomodoroConfig) {
        config = newConfig
        resetToFocus()
    }

    override fun start() {
        if (_state.value.isRunning) return
        _state.value = _state.value.copy(isRunning = true)
        startTicker()
    }

    override fun pause() {
        if (!_state.value.isRunning) return
        _state.value = _state.value.copy(isRunning = false)
        stopTicker()
    }

    override fun resetToFocus() {
        stopTicker()
        _state.value = PomodoroState(
            phase = PomodoroPhase.Focus,
            remainingSeconds = defaultSecondsFor(PomodoroPhase.Focus),
            isRunning = false,
            completedPomodoros = 0
        )
    }

    override fun skipPhase() {
        stopTicker()
        advancePhase(skipped = true)
    }

    private fun startTicker() {
        stopTicker()
        tickerJob = scope.launch {
            while (isActive && _state.value.isRunning) {
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
