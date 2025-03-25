package com.himanshu.semhub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.himanshu.semhub.R

@Composable
fun GoogleSignInButton(
    text: String = "Google",
    onClick: () -> Unit,
    enabled: Boolean = true // ✅ Add enabled parameter
) {
    Box(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth(0.5f)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(25.dp),
            )
            .background(
                color = if (enabled) Color(0xE8EFE7E7) else Color.Gray, // ✅ Change color if disabled
                shape = RoundedCornerShape(25.dp)
            )
            .clickable(enabled = enabled) { onClick() }, // ✅ Disable clicks when false
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.google),
            contentDescription = "Google Logo",
            modifier = Modifier
                .height(30.dp)
                .padding(5.dp),
            contentScale = ContentScale.Fit
        )
    }
}
