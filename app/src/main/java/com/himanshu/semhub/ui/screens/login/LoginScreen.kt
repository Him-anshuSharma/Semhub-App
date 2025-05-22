package com.himanshu.semhub.ui.screens.login

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.himanshu.semhub.ui.screens.login.components.GoogleSignInButton
import com.himanshu.semhub.ui.viewmodel.AuthViewModel
import com.himanshu.semhub.ui.viewmodel.LoginState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController = rememberNavController(),
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val loginState by viewModel.loginState.collectAsState()

    viewModel.check()

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                navController.navigate("onboarding") {
                    popUpTo("login") { inclusive = true }
                }
            }

            is LoginState.Error -> {
                Log.e("LoginError", (loginState as LoginState.Error).message)
            }

            else -> {}
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Logo or Icon could go here
                Spacer(modifier = Modifier.height(32.dp))

                // Welcome Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Welcome Header
                        Text(
                            text = "Welcome to SemHub",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Description
                        Text(
                            text = "Get your timetable instantly with just one click! Sign in with your Google account for a seamless experience.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Sign In Button
                        val isLoading = loginState is LoginState.Loading
                        GoogleSignInButton(
                            onClick = { viewModel.login() },
                            enabled = !isLoading
                        )

                        // Login State Handling
                        when (loginState) {
                            is LoginState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            is LoginState.Error -> {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = (loginState as LoginState.Error).message,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            is LoginState.Success -> {
                                LaunchedEffect(loginState) {
                                    Log.d("LoginScreen", "Success - Navigating to Home")
                                }
                            }
                            else -> {}
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
