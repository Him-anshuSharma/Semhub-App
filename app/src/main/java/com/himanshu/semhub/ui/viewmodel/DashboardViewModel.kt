package com.himanshu.semhub.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himanshu.semhub.data.model.DashboardSummary
import com.himanshu.semhub.data.model.Goal
import com.himanshu.semhub.data.model.Task
import com.himanshu.semhub.data.repository.GoalsTasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val goalTaskRepository: GoalsTasksRepository
) : ViewModel() {

    val TAG = "DashboardViewModel"

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks = _tasks.asStateFlow()

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals = _goals.asStateFlow()

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val screenState = _screenState.asStateFlow()

    init {
        getLatestTaskAndGoals()
    }

    suspend fun getDashboardSummary(): DashboardSummary {
        val goalCount = goalTaskRepository.getTotalGoals()
        val taskCount = goalTaskRepository.getTotalTasks()
        return DashboardSummary(goalCount, taskCount)
    }

    fun getLatestTaskAndGoals() {
        _screenState.value = ScreenState.Loading
        viewModelScope.launch {
            try {
                val tasks = goalTaskRepository.getTasks()
                Log.d(TAG, "Loaded tasks: ${tasks.size}")
                _tasks.value = tasks
                for(task in tasks){
                    val subtasks = task.subtasks
                    Log.d(TAG, "Subtasks for task ${task.title}: $subtasks")
                }

                val goals = goalTaskRepository.getGoals()
                Log.d(TAG, "Loaded goals: ${goals.size}")
                _goals.value = goals

                _screenState.value = ScreenState.Success
            } catch (e: Exception) {
                Log.e(TAG, "Error loading data", e)
                _screenState.value = ScreenState.Error("Failed to load data: ${e.message}")
            }
        }
    }
}

sealed class ScreenState {
    data object Loading : ScreenState()
    data object Success : ScreenState()
    data class Error(val message: String) : ScreenState()
}
