package com.himanshu.semhub.ui.screens.goalDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himanshu.semhub.data.model.Goal
import com.himanshu.semhub.data.model.Task
import com.himanshu.semhub.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class GoalDetailsViewModel @Inject constructor(
    private val repository: UserDataRepository
) : ViewModel() {

    private val _selectedGoal = MutableStateFlow<Goal?>(null)
    val selectedGoal: StateFlow<Goal?> = _selectedGoal

    private val _associatedTasks = MutableStateFlow<List<Task>>(emptyList())
    val associatedTasks: StateFlow<List<Task>> = _associatedTasks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadGoalDetails(goalId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Fetch the goal details
                val goal = repository.getGoalById(goalId)
                _selectedGoal.value = goal

                // If goal exists, fetch associated tasks
                if (goal != null) {
                    val taskIds = goal.targetTasks ?: emptyList()
                    val tasks = mutableListOf<Task>()
                    for (taskId in taskIds) {
                        val task = repository.getTaskById(taskId.toInt())
                        if (task != null) {
                            tasks.add(task)
                        }
                    }
                    _associatedTasks.value = tasks
                } else {
                    _error.value = "Goal not found"
                    _associatedTasks.value = emptyList()
                }
            } catch (e: Exception) {
                _error.value = "Failed to load details: ${e.message}"
                _associatedTasks.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}