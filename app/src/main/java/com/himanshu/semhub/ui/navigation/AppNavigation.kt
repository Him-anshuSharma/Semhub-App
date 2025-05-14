package com.himanshu.semhub.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.himanshu.semhub.ui.screens.dashboard.DashboardScreen
import com.himanshu.semhub.ui.screens.login.LoginScreen
import com.himanshu.semhub.ui.screens.onboarding.OnboardingScreen
import com.himanshu.semhub.ui.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val startDestination = if (authViewModel.getProfile() != null) "onboarding" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("onboarding") {
            OnboardingScreen(
                navController
            )
        }
        composable("dashboard"){
            DashboardScreen(
                navController
            )
        }
    }
}

object Routes{
    val Login = "login"
    val Onboarding = "onboarding"
    val Dashboard = "dashboard"
}
