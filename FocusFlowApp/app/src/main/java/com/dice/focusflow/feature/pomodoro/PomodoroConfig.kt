package com.dice.focusflow.feature.pomodoro

data class PomodoroConfig(
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val cyclesUntilLongBreak: Int = 4
)
