package com.himanshu.semhub.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.himanshu.semhub.viewmodel.AuthViewModel
import com.himanshu.semhub.viewmodel.LoginState

@Composable
fun LoginScreen(
    navController: NavController = rememberNavController(),
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                navController.navigate("profile") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is LoginState.Error -> {
                Log.e("LoginError", (loginState as LoginState.Error).message)
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { viewModel.login() },
            enabled = loginState != LoginState.Loading
        ) {
            Text(
                text = if (loginState == LoginState.Loading) "Signing In..." else "Sign in with Google"
            )
        }
    }
}
