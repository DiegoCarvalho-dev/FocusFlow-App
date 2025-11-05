package com.dice.focusflow.feature.pomodoro.engine

import com.dice.focusflow.feature.pomodoro.PomodoroConfig
import com.dice.focusflow.feature.pomodoro.PomodoroPhase
import com.dice.focusflow.feature.pomodoro.PomodoroState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PomodoroEngineImpl(
    private var config: PomodoroConfig,
    private val scope: CoroutineScope
) : PomodoroEngine {

    private val _state = MutableStateFlow(createInitialState())
    override val state: StateFlow<PomodoroState> = _state.asStateFlow()

    private var tickerJob: Job? = null

    override fun setConfig(newConfig: PomodoroConfig) {
        if (newConfig == config) return

        config = newConfig

        val current = _state.value
        stopTicker()
        _state.value = current.copy(
            phase = PomodoroPhase.Focus,
            remainingSeconds = newConfig.focusMinutes * 60,
            isRunning = false
        )
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
        _state.value = createInitialState()
    }

    override fun skipPhase() {
        stopTicker()
        advancePhase(skipped = true)
    }

    override fun destroy() {
        scope.cancel()
    }

    private fun startTicker() {
        stopTicker()
        tickerJob = scope.launch(Dispatchers.Default) {
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
        val current = _state.value
        if (!current.isRunning) return

        val nextSecond = current.remainingSeconds - 1
        if (nextSecond > 0) {
            _state.value = current.copy(remainingSeconds = nextSecond)
        } else {
            advancePhase()
        }
    }

    private fun advancePhase(skipped: Boolean = false) {
        val current = _state.value
        when (current.phase) {
            PomodoroPhase.Focus -> {
                val completed = if (skipped) current.completedPomodoros else current.completedPomodoros + 1
                val nextPhase =
                    if (completed > 0 && completed % config.cyclesUntilLongBreak == 0) {
                        PomodoroPhase.LongBreak
                    } else {
                        PomodoroPhase.ShortBreak
                    }

                _state.value = current.copy(
                    phase = nextPhase,
                    remainingSeconds = defaultSecondsFor(nextPhase),
                    isRunning = false,
                    completedPomodoros = completed
                )
            }

            PomodoroPhase.ShortBreak,
            PomodoroPhase.LongBreak -> {
                _state.value = current.copy(
                    phase = PomodoroPhase.Focus,
                    remainingSeconds = defaultSecondsFor(PomodoroPhase.Focus),
                    isRunning = false
                )
            }
        }
    }

    private fun createInitialState() = PomodoroState(
        phase = PomodoroPhase.Focus,
        remainingSeconds = config.focusMinutes * 60,
        isRunning = false,
        completedPomodoros = 0
    )

    private fun defaultSecondsFor(phase: PomodoroPhase): Int = when (phase) {
        PomodoroPhase.Focus -> config.focusMinutes * 60
        PomodoroPhase.ShortBreak -> config.shortBreakMinutes * 60
        PomodoroPhase.LongBreak -> config.longBreakMinutes * 60
    }
}
