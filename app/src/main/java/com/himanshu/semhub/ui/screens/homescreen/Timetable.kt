package com.himanshu.semhub.ui.screens.homescreen

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.himanshu.semhub.viewmodel.HomeViewModel
import java.io.File

@Composable
fun Timetable(
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
) {
    val context = LocalContext.current
    val timetableState by homeViewModel.timetableState.collectAsState()
    val fileUri = remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        fileUri.value = uri
    }

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
        Text(text = timetableState?.toString() ?: "No Timetable Available")

        Button(onClick = {
            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            Text("Upload")
        }
    }
}

fun uriToFile(context: Context, uri: Uri): File? {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri) ?: return null

    val fileExtension = when (contentResolver.getType(uri)) {
        "image/jpeg" -> ".jpg"
        "image/png" -> ".png"
        else -> return null
    }

    val file = File(context.cacheDir, "temp_file_${System.currentTimeMillis()}$fileExtension")
    file.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    return file
}
