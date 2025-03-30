package com.himanshu.semhub.ui.screens.homescreen.fragments

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.himanshu.semhub.ui.screens.homescreen.components.TimeTableCard
import com.himanshu.semhub.utils.getCurrentDay
import com.himanshu.semhub.utils.uriToFile
import com.himanshu.semhub.viewmodel.HomeViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Timetable(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val timetableState by homeViewModel.timetableState.collectAsState()
    val fileUri = remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        fileUri.value = uri
    }

    //observe URI
    LaunchedEffect(fileUri.value) {
        fileUri.value?.let { uri ->
            Log.d("Timetable", "File selected: $uri")
            val file = uriToFile(context, uri)
            file?.let {
                Log.d("Timetable", "Converted file: ${it.name}")
                homeViewModel.getTimeTable(it)
            }
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(timetableState == null){
            Text(text = "No Timetable Available")
            Button(onClick = {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) {
                Text("Upload")
            }
        }
        else{
            val today = getCurrentDay()
            Log.d("anshu",today)// Function should return a string like "Monday"
            val subjectList = homeViewModel.getTimeTableDayWise(today)

            Log.d("anshu",subjectList.toString())

            if (subjectList.isNullOrEmpty()) {
                Text(text = "No classes today.")
            } else {
                // Show Timetable in LazyColumn
                LazyColumn {
                    items(
                        count = subjectList.size,
                        itemContent = { index ->
                            val time = subjectList[index].time
                            val subject = subjectList[index].subject
                            TimeTableCard(time = time, subject = subject)
                        }
                    )
                }
            }
        }
    }
}




