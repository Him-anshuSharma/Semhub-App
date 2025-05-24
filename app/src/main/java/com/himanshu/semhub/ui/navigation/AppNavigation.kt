package com.himanshu.semhub.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.himanshu.semhub.ui.screens.dashboard.DashboardScreen
import com.himanshu.semhub.ui.screens.login.LoginScreen
import com.himanshu.semhub.ui.screens.onboarding.OnboardingScreen
import com.himanshu.semhub.ui.screens.tasksgoals.GoalTaskDetailScreen
import com.himanshu.semhub.ui.screens.tasksgoals.TasksGoalsScreen
import com.himanshu.semhub.ui.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val startDestination = if (authViewModel.getProfile() != null) Routes.ONBOARDING else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(navController)
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(navController)
        }
        composable(Routes.GOAL_TASK) {
            TasksGoalsScreen(navController)
        }
        composable(
            route = "${Routes.GOAL_TASK_DETAIL}/{goalId}",
            arguments = listOf(navArgument("goalId") { type = NavType.IntType })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getInt("goalId") ?: 0
            GoalTaskDetailScreen(goalId = goalId, navController = navController)
        }
    }
}

// Define Routes as an object for better organization

