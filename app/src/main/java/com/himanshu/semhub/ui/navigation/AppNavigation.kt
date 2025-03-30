package com.himanshu.semhub.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.himanshu.semhub.ui.screens.homescreen.HomeScreen
import com.himanshu.semhub.ui.screens.login.LoginScreen

@Composable
fun AppNavigation(
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("homescreen") {
            HomeScreen()
        }
    }
}