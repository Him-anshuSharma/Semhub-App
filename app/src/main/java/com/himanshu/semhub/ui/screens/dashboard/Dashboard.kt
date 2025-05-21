//package com.himanshu.semhub.ui.screens.dashboard
//
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.himanshu.semhub.data.model.DashboardSummary
//import com.himanshu.semhub.ui.screens.dashboard.components.DashboardContent
//
//@Composable
//fun DashboardScreen(
//    navController: NavController,
//    viewModel: DashboardViewModel = hiltViewModel()
//) {
//    val dashboardSummary by viewModel.dashboardSummary.collectAsState()
//    val tasks by viewModel.tasks.collectAsState()
//    val goals by viewModel.goals.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val error by viewModel.error.collectAsState()
//
//    DashboardContent(
//        dashboardSummary = dashboardSummary,
//        tasks = tasks,
//        goals = goals,
//        isLoading = isLoading,
//        error = error,
//        onErrorDismiss = { viewModel.clearError() },
//        onRefresh = { viewModel.refreshData() }
//    )
//}
//
//
//
//@Preview(showBackground = true)
//@Composable
//fun DashboardScreenPreview() {
//    MaterialTheme {
//        // Create sample data for preview
//        val sampleTasks = listOf(
//            Task(
//                id = 1,
//                title = "Complete Math Assignment",
//                type = "Academic",
//                subject = "Mathematics",
//                priority = "High",
//                deadline = "Tomorrow"
//            ),
//            Task(
//                id = 2,
//                title = "Prepare Presentation",
//                type = "Work",
//                subject = "Project",
//                priority = "Medium",
//                deadline = "Next Week"
//            )
//        )
//
//        val sampleGoals = listOf(
//            Goal(
//                id = 1,
//                name = "Graduate with Honors",
//                type = "Academic",
//                targetDate = "May 2026"
//            ),
//            Goal(
//                id = 2,
//                name = "Learn Kotlin Programming",
//                type = "Skill Development",
//                targetDate = "December 2025"
//            )
//        )
//
//        val sampleSummary = DashboardSummary(
//            taskCount = 4,
//            goalCount = 2
//        )
//
//        DashboardContent(
//            dashboardSummary = sampleSummary,
//            tasks = sampleTasks,
//            goals = sampleGoals,
//            isLoading = false,
//            error = null,
//            onErrorDismiss = {},
//            onRefresh = {}
//        )
//    }
//}
