package com.dice.focusflow.feature.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TasksViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val MAX_TASKS = 10
    }

    private val repo = TasksRepository(application)

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        viewModelScope.launch {
            repo.tasksFlow.collectLatest { saved ->
                _tasks.value = saved
            }
        }
    }

    private fun saveNow() {
        viewModelScope.launch {
            repo.saveTasks(_tasks.value)
        }
    }

    fun addTask(title: String) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return
        if (_tasks.value.size >= MAX_TASKS) return
        if (_tasks.value.any { it.title.equals(trimmed, ignoreCase = true) }) return

        val newTask = Task(
            id = System.currentTimeMillis(),
            title = trimmed
        )
        _tasks.value = _tasks.value + newTask
        saveNow()
    }

    fun toggleDone(id: Long) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == id) task.copy(isDone = !task.isDone) else task
        }
        saveNow()
    }

    fun removeTask(id: Long) {
        _tasks.value = _tasks.value.filterNot { it.id == id }
        saveNow()
    }

    fun clearAll() {
        _tasks.value = emptyList()
        saveNow()
    }
}
