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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.himanshu.semhub.data.model.timetable.SubjectSchedule
import com.himanshu.semhub.data.model.timetable.Timetable
import com.himanshu.semhub.ui.screens.homescreen.components.TimeTableCard
import com.himanshu.semhub.utils.getCurrentDay
import com.himanshu.semhub.utils.uriToFile
import com.himanshu.semhub.ui.viewmodel.timetable.TimeTableViewModel
import com.himanshu.semhub.ui.viewmodel.timetable.TimetableState
import java.sql.Time

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Timetable(
    timeTableViewModel: TimeTableViewModel = hiltViewModel()
) {
    val TAG = "TimeTableFragment"
    val context = LocalContext.current

    // Observing timetable state from ViewModel
    val timetableState by timeTableViewModel.timetableState.collectAsState()

    // Convert fileUri into Compose state
    val fileUri = remember { mutableStateOf<Uri?>(null) }

    // Derived state: Determines if the upload button should be visible
    val uploadButtonVisible by remember { derivedStateOf { timetableState == TimetableState.Idle } }

    // Derived state: Extract subject list when timetable is available
    val subjectList by remember {
        derivedStateOf {
            if (timetableState is TimetableState.Success) {
                timeTableViewModel.getTimeTableDayWise(getCurrentDay())
            } else null
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        fileUri.value = uri
    }



    // Check if timetable already exists when the Composable loads
    LaunchedEffect(Unit) {
        timeTableViewModel.ifTimeTableExists()
    }

    // Observe fileUri changes and process the file
    LaunchedEffect(fileUri.value) {
        fileUri.value?.let { uri ->
            val file = uriToFile(context, uri)
            file?.let {
                timeTableViewModel.getTimeTable(it)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (subjectList.isNullOrEmpty()) {
            timeTableViewModel.ifTimeTableExists()
            Text(
                text = when (timetableState) {
                    is TimetableState.Error -> (timetableState as TimetableState.Error).message
                    is TimetableState.Loading -> "Loading.."
                    else -> "No Timetable Available"
                }
            )
            if (uploadButtonVisible) {
                Button(onClick = { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                    Text("Upload File")
                }
            } else if(timetableState != TimetableState.Loading) {
                Button(onClick = {
                    timeTableViewModel.resetState()
                    fileUri.value = null
                }) {
                    Text("Retry")
                }
            }
        } else {
            Log.d(TAG, "Timetable is there")
            LazyColumn {
                items(subjectList!!.size) { index ->
                    val subject = subjectList!![index]
                    TimeTableCard(time = subject.time, subject = subject.subject)
                }
            }
        }
    }
}
