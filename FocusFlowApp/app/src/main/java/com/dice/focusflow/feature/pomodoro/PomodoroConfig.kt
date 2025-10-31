package com.dice.focusflow.feature.pomodoro

data class PomodoroConfig(
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val longBreakEvery: Int = 4
) {
    fun totalSecondsFor(phase: PomodoroPhase): Int = when (phase) {
        PomodoroPhase.FOCUS -> focusMinutes * 60
        PomodoroPhase.SHORT_BREAK -> shortBreakMinutes * 60
        PomodoroPhase.LONG_BREAK -> longBreakMinutes * 60
    }
}