package com.himanshu.semhub.ui.screens.homescreen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.himanshu.semhub.viewmodel.HomeViewModel
import java.io.File

@Preview(showBackground = true)
@Composable
fun Timetable(
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
){

    val context = LocalContext.current

    val timetableState by homeViewModel.timetableState.collectAsState()
    val file = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        file.value = it
    }

    file.value?.let { uri->
        LaunchedEffect(uri) {
            val _file = uriToFile(context,uri)
            _file?.let {it ->
                homeViewModel.getTimeTable(it)
            }
        }
    }


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = timetableState?.toString() ?: "No Timetable Available")

        Button(onClick = {
            launcher.launch(
                PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
            )

        }) {
            Text(text = timetableState.toString())

        }
    }

    file.value.let {

    }
}

fun uriToFile(context: Context, uri: Uri): File? {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri) ?: return null
    val file = File(context.cacheDir, "temp_file_${System.currentTimeMillis()}.jpg") // Change extension as needed
    file.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    return file
}