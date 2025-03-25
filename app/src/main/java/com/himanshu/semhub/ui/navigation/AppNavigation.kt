package com.himanshu.semhub.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.credentials.CredentialManager
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.himanshu.semhub.ui.screens.LoginScreen
import com.himanshu.semhub.ui.screens.ProfileScreen

@Composable
fun AppNavigation(
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen()
        }
    }
}