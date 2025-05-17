package com.himanshu.semhub.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himanshu.semhub.data.model.DashboardSummary
import com.himanshu.semhub.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    // State for dashboard summary
    private val _dashboardSummary = MutableStateFlow(DashboardSummary(0, 0))
    val dashboardSummary: StateFlow<DashboardSummary> = _dashboardSummary.asStateFlow()

    // State for tasks
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // State for goals
    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Initialize the ViewModel
    init {
        loadDashboardData()
    }

    // Load all dashboard data
    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Load dashboard summary
                dashboardRepository.getDashboardSummary().collect { summary ->
                    _dashboardSummary.value = summary
                }

                // Load tasks
                dashboardRepository.getAllTasks().collect { taskList ->
                    _tasks.value = taskList
                }

                // Load goals
                dashboardRepository.getAllGoals().collect { goalList ->
                    _goals.value = goalList
                }
            } catch (e: Exception) {
                _error.value = "Failed to load dashboard data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Get task statistics
    fun loadTaskStats() {
        viewModelScope.launch {
            try {
                dashboardRepository.getTaskStats().collect { stats ->
                    // Handle task stats
                }
            } catch (e: Exception) {
                _error.value = "Failed to load task statistics: ${e.message}"
            }
        }
    }

    // Get goal statistics
    fun loadGoalStats() {
        viewModelScope.launch {
            try {
                dashboardRepository.getGoalStats().collect { stats ->
                    // Handle goal stats
                }
            } catch (e: Exception) {
                _error.value = "Failed to load goal statistics: ${e.message}"
            }
        }
    }

    // Refresh dashboard data
    fun refreshData() {
        loadDashboardData()
    }

    // Clear error
    fun clearError() {
        _error.value = null
    }
}

