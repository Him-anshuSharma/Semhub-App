package com.himanshu.semhub.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.himanshu.semhub.ui.screens.homescreen.HomeScreen
import com.himanshu.semhub.ui.screens.login.LoginScreen
import com.himanshu.semhub.ui.viewmodel.auth.AuthViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val startDestination = if (authViewModel.getProfile() != null) "homescreen" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("homescreen") {
            HomeScreen()
        }
    }
}