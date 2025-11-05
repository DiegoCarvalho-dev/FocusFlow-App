package com.dice.focusflow.feature.summary

data class DailySummary(
    val date: String,
    val focusSeconds: Long,
    val completedFocusSessions: Int
)
