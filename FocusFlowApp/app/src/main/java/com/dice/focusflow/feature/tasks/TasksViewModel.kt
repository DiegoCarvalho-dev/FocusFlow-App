package com.dice.focusflow.feature.tasks

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TasksViewModel : ViewModel() {

    companion object {
        private const val MAX_TASKS = 10
    }

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    fun addTask(title: String) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return
        if (_tasks.value.size >= MAX_TASKS) return

        val newTask = Task(
            id = System.currentTimeMillis(),
            title = trimmed
        )
        _tasks.value = _tasks.value + newTask
    }

    fun toggleDone(id: Long) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == id) task.copy(isDone = !task.isDone) else task
        }
    }

    fun removeTask(id: Long) {
        _tasks.value = _tasks.value.filterNot { it.id == id }
    }

    fun clearAll() {
        _tasks.value = emptyList()
    }
}
