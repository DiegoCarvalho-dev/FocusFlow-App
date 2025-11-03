package com.dice.focusflow.feature.pomodoro

import kotlinx.coroutines.flow.StateFlow

interface PomodoroEngine {
    val state: StateFlow<PomodoroState>

    fun setConfig(newConfig: PomodoroConfig)
    fun start()
    fun pause()
    fun resetToFocus()
    fun skipPhase()
}
