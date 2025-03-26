package com.himanshu.semhub.ui.screens.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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

@Composable
fun HomeScreen(){
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
                            onClick = {}
                        ) {
                            Icon(Icons.Filled.DateRange, "Localized description")
                        }
                        FloatingActionButton(
                            onClick = {}
                        ) {
                            Icon(Icons.Filled.Email, "Localized description")
                        }
                        FloatingActionButton(
                            onClick = {}
                        ) {
                            Icon(Icons.Filled.Face, "Localized description")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Profile()
        }
    }
}

@Composable
@Preview(showBackground = true)
fun Preview(){
    HomeScreen()
}