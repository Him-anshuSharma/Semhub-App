package com.himanshu.semhub.ui.screens.homescreen.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MessageCard(message: String, left: Boolean, color: Color = Color.LightGray) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (left) Arrangement.Start else Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .padding(5.dp)
                .widthIn(max = screenWidth * 0.7f) // Limit width to 30% of screen
                .clip(RoundedCornerShape(20.dp))
                .background(color = if(left) Color.LightGray else Color(0xffecdcfc))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(5.dp),
                textAlign = if (left) TextAlign.Left else TextAlign.Right,
                color = Color.Black
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun Preview() {
    Column {
        MessageCard("Short", false)
        MessageCard("This is a longer message that should be limited to 30% of screen width", true)
    }
}
