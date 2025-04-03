package com.himanshu.semhub.ui.screens.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.himanshu.semhub.ui.screens.homescreen.components.RotatingWheel
import com.himanshu.semhub.ui.screens.homescreen.fragments.ChatFragment
import com.himanshu.semhub.ui.screens.homescreen.fragments.Profile
import com.himanshu.semhub.ui.screens.homescreen.fragments.Timetable
import com.himanshu.semhub.ui.viewmodel.timetable.TimeTableViewModel

@Composable
fun HomeScreen(){
    val bottomNavController = rememberNavController()
    val viewModel: TimeTableViewModel = hiltViewModel()
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0x00FF0000),
                tonalElevation = 10.dp,
                actions = {

                },
                floatingActionButton = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
                        FloatingActionButton(
                            onClick = {
                                bottomNavController.navigate("timetable")
                            }
                        ) {
                            Icon(Icons.Filled.DateRange, "TimeTable")
                        }
                        FloatingActionButton(
                            onClick = {
                                bottomNavController.navigate("chats")
                            }
                        ) {
                            Icon(Icons.Filled.Email, "Chat")
                        }
                        FloatingActionButton(
                            onClick = {
                                bottomNavController.navigate("profile")
                            }
                        ) {
                            Icon(Icons.Filled.Face, "Profile")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = "timetable",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("profile") { Profile() }
            composable("timetable") { Timetable(viewModel,bottomNavController) }
            composable("cards") { RotatingWheel(viewModel,bottomNavController) }
            composable("chats"){ ChatFragment() }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun Preview(){
    HomeScreen()
}