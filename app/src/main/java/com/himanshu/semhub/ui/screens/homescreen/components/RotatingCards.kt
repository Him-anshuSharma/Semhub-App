package com.himanshu.semhub.ui.screens.homescreen.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.himanshu.semhub.ui.viewmodel.timetable.TimeTableViewModel
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RotatingWheel(
    timeTableViewModel: TimeTableViewModel,
    navController: NavHostController
) {
    val n = 5
    val radius = 102.86f
    var rotationAngle by remember { mutableFloatStateOf(0f) }

    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val angleChange = atan2(dragAmount.y, dragAmount.x) * (180 / PI).toFloat()
                    rotationAngle += angleChange
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size((radius * 2.5f).dp),
            contentAlignment = Alignment.Center
        ) {
            for (i in 0 until n) {
                val angle = (2 * PI / n * i).toFloat() + rotationAngle.toRadians()
                val x = radius * cos(angle)
                val y = radius * sin(angle)

                Card(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(60.dp)
                        .offset(x.dp, y.dp)
                        .fillMaxSize()
                    ,
                    onClick = {
                        Log.d("RotatingWheel", "Clicked on ${daysOfWeek[i]}")
                        timeTableViewModel.updateSelectedDay(getDay(daysOfWeek[i]).lowercase())
                        navController.navigate("timetable")
                    }
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = daysOfWeek[i],
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

fun Float.toRadians(): Float = (this * PI / 180).toFloat()

fun getDay(day:String):String{
    return when(day){
        "Tue" -> return "Tuesday"
        "Wed" -> return "Wednesday"
        "Thu" -> return "Thursday"
        "Fri" -> return "Friday"
        else -> "Monday"
    }
}
