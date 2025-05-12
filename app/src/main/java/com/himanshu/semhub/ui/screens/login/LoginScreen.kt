package com.himanshu.semhub.ui.screens.login

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.himanshu.semhub.R
import com.himanshu.semhub.ui.screens.login.components.GoogleSignInButton
import com.himanshu.semhub.ui.viewmodel.AuthViewModel
import com.himanshu.semhub.ui.viewmodel.LoginState

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

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Row(modifier = Modifier.padding(10.dp)) {
            Spacer(modifier = Modifier.weight(0.4f)) // Empty space on the left

            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.fillMaxHeight(0.3f)) // Top spacing

                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.6f)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    // Main Box (Login Info)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xfffbf0e8), shape = RoundedCornerShape(5.dp))
                            .border(
                                BorderStroke(3.dp, Color(0xff988f90)),
                                shape = RoundedCornerShape(5.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "\uD83D\uDCC5 Get your timetable instantly with just one click! Sign in with your Google account for a seamless experience. Welcome aboard! \uD83C\uDF89",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )

                            val isLoading = loginState is LoginState.Loading
                            GoogleSignInButton(
                                onClick = { viewModel.login() },
                                enabled = !isLoading // ✅ Disable button while loading
                            )

                            when (loginState) {
                                is LoginState.Loading -> CircularProgressIndicator()
                                is LoginState.Error -> {
                                    Text(
                                        (loginState as LoginState.Error).message,
                                        color = Color.Red
                                    )
                                }

                                is LoginState.Success -> {
                                    LaunchedEffect(loginState) { // ✅ Trigger every state change
                                        Log.d("LoginScreen", "Success - Navigating to Home")
                                    }
                                }

                                else -> {}
                            }
                        }


                    }
                    // Header Box
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-15).dp)
                            .fillMaxWidth(0.5f)
                            .background(Color(0xffb0dcd3), shape = RoundedCornerShape(5.dp))
                            .border(
                                BorderStroke(3.dp, Color(0xff988f90)),
                                shape = RoundedCornerShape(5.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Hey, Welcome",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp) // ✅ Increased padding for better visibility
                        )
                    }

                }
                Spacer(modifier = Modifier.fillMaxHeight(0.5f)) // Bottom spacing
            }
        }
    }
}

