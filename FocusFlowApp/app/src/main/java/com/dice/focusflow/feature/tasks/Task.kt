package com.dice.focusflow.feature.tasks

data class Task(
    val id: Long,
    val title: String,
    val isDone: Boolean = false
)
