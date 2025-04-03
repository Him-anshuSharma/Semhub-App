package com.himanshu.semhub.ui.screens.homescreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MessageCard(message: String, left: Boolean, color: Color = Color.LightGray){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .background(color = color)
        ,

        contentAlignment = if (left) Alignment.CenterStart else Alignment.CenterEnd
    ){
        Text(
            text = message,
            modifier = Modifier.background(color).padding(horizontal = 10.dp, vertical = 2.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun Preview(){
    Column {
        MessageCard("Hi",false)
        MessageCard("Hello",true)
    }
}