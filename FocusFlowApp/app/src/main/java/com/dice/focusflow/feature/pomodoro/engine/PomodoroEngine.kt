package com.dice.focusflow.feature.pomodoro.engine

import com.dice.focusflow.feature.pomodoro.PomodoroConfig
import com.dice.focusflow.feature.pomodoro.PomodoroState
import kotlinx.coroutines.flow.StateFlow

interface PomodoroEngine {
    val state: StateFlow<PomodoroState>
    fun setConfig(newConfig: PomodoroConfig)
    fun start()
    fun pause()
    fun resetToFocus()
    fun skipPhase()
}
