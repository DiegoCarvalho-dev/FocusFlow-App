package com.dice.focusflow.feature.pomodoro

data class PomodoroState(
    val phase: PomodoroPhase,
    val remainingSeconds: Int,
    val isRunning: Boolean,
    val completedPomodoros: Int
)