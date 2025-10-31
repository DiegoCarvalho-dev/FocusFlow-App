package com.dice.focusflow.feature.pomodoro

data class PomodoroState(
    val phase: PomodoroPhase = PomodoroPhase.FOCUS,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val completedPomodoros: Int = 0,
    val cycleCount: Int = 0
)