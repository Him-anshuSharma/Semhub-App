package com.himanshu.semhub.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himanshu.semhub.data.model.Goal
import com.himanshu.semhub.data.model.Task
import com.himanshu.semhub.data.repository.GoalsTasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskGoalsViewModel @Inject constructor(
    private val repository: GoalsTasksRepository
) : ViewModel() {

    private val TAG = "TaskGoalsViewModel"

    // UI state for tasks
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // UI state for goals
    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()

    // UI state for filtering
    private val _currentFilter = MutableStateFlow<Filter>(Filter.All)
    val currentFilter: StateFlow<Filter> = _currentFilter.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Selected tab
    private val _selectedTab = MutableStateFlow(Tab.TASKS)
    val selectedTab: StateFlow<Tab> = _selectedTab.asStateFlow()

    init {
        loadData()
    }

    /**
     * Load all tasks and goals data
     */
    fun loadData() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                when (_selectedTab.value) {
                    Tab.TASKS -> {
                        val tasksList = repository.getTasks()
                        _tasks.value = tasksList
                        Log.d(TAG, "Loaded ${tasksList.size} tasks")
                    }
                    Tab.GOALS -> {
                        val goalsList = repository.getGoals()
                        _goals.value = goalsList
                        Log.d(TAG, "Loaded ${goalsList.size} goals")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading data", e)
                _error.value = "Failed to load data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Set the selected tab
     */
    fun setTab(tab: Tab) {
        if (_selectedTab.value != tab) {
            _selectedTab.value = tab
            loadData()
        }
    }

    /**
     * Apply filter to tasks or goals
     */
    fun setFilter(filter: Filter) {
        _currentFilter.value = filter
        loadData()
    }

    /**
     * Delete a task
     */
    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteTask(taskId)
                // Refresh tasks list after deletion
                val updatedTasks = repository.getTasks()
                _tasks.value = updatedTasks
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting task", e)
                _error.value = "Failed to delete task: ${e.message}"
            }
        }
    }

    /**
     * Delete a goal
     */
    fun deleteGoal(goalId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteGoal(goalId)
                // Refresh goals list after deletion
                val updatedGoals = repository.getGoals()
                _goals.value = updatedGoals
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting goal", e)
                _error.value = "Failed to delete goal: ${e.message}"
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Enum representing the tabs in the UI
     */
    enum class Tab {
        TASKS,
        GOALS
    }

    fun updateTaskStatus(taskId: Int, completed: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateTaskStatus(taskId, completed)
                // Refresh tasks list
                val updatedTasks = repository.getTasks()
                _tasks.value = updatedTasks
            } catch (e: Exception) {
                Log.e(TAG, "Error updating task status", e)
                _error.value = "Failed to update task: ${e.message}"
            }
        }
    }

    fun updateTaskPriority(taskId: Int, priority: String) {
        viewModelScope.launch {
            try {
                repository.updateTaskPriority(taskId, priority)
                // Refresh tasks list
                val updatedTasks = repository.getTasks()
                _tasks.value = updatedTasks
            } catch (e: Exception) {
                Log.e(TAG, "Error updating task priority", e)
                _error.value = "Failed to update task: ${e.message}"
            }
        }
    }

    fun updateGoalStatus(goalId: Int, completed: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateGoalStatus(goalId, completed)
                // Refresh goals list
                val updatedGoals = repository.getGoals()
                _goals.value = updatedGoals
            } catch (e: Exception) {
                Log.e(TAG, "Error updating goal status", e)
                _error.value = "Failed to update goal: ${e.message}"
            }
        }
    }


    /**
     * Sealed class representing filter options
     */
    sealed class Filter {
        object All : Filter()
        object High : Filter()
        object Medium : Filter()
        object Low : Filter()
        data class BySubject(val subject: String) : Filter()
        data class ByType(val type: String) : Filter()
    }
}
