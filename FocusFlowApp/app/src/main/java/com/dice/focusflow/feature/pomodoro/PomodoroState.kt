package com.dice.focusflow.feature.pomodoro

data class PomodoroState(
    val phase: PomodoroPhase = PomodoroPhase.Focus,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val completedPomodoros: Int = 0
)
