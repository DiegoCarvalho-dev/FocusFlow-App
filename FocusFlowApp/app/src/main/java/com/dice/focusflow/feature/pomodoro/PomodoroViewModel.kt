package com.dice.focusflow.feature.pomodoro

import androidx.lifecycle.ViewModel
import com.dice.focusflow.feature.pomodoro.engine.PomodoroEngine
import kotlinx.coroutines.flow.StateFlow

class PomodoroViewModel(
    private val engine: PomodoroEngine
) : ViewModel() {

    val state: StateFlow<PomodoroState> = engine.state

    fun setConfig(newConfig: PomodoroConfig) {
        engine.setConfig(newConfig)
    }

    fun start() = engine.start()

    fun pause() = engine.pause()

    fun resetToFocus() = engine.resetToFocus()

    fun skipPhase() = engine.skipPhase()

    override fun onCleared() {
        super.onCleared()
    }
}
