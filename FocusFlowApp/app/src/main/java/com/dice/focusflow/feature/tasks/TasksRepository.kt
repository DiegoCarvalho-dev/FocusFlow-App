package com.dice.focusflow.feature.tasks

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore("tasks_prefs")

class TasksRepository(private val context: Context) {

    companion object {
        private val KEY_TASKS = stringPreferencesKey("tasks_json")
    }

    val tasksFlow: Flow<List<Task>> = context.dataStore.data.map { prefs ->
        prefs[KEY_TASKS]?.let { json ->
            val array = JSONArray(json)
            List(array.length()) { i ->
                val obj = array.getJSONObject(i)
                Task(
                    id = obj.getLong("id"),
                    title = obj.getString("title"),
                    isDone = obj.getBoolean("isDone")
                )
            }
        } ?: emptyList()
    }

    suspend fun saveTasks(tasks: List<Task>) {
        context.dataStore.edit { prefs ->
            val array = JSONArray()
            tasks.forEach { task ->
                val obj = JSONObject().apply {
                    put("id", task.id)
                    put("title", task.title)
                    put("isDone", task.isDone)
                }
                array.put(obj)
            }
            prefs[KEY_TASKS] = array.toString()
        }
    }
}
