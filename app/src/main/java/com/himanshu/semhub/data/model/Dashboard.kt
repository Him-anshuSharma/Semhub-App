package com.himanshu.semhub.data.model

/**
 * Data class to hold dashboard summary information
 */
data class DashboardSummary(
    val taskCount: Int = 0,
    val goalCount: Int = 0
)

/**
 * Data class for task statistics
 */
data class TaskStats(
    val total: Int,
    val completed: Int,
    val pending: Int,
    val completionRate: Float
)

/**
 * Data class for goal statistics
 */
data class GoalStats(
    val total: Int,
    val completed: Int,
    val pending: Int,
    val completionRate: Float
)