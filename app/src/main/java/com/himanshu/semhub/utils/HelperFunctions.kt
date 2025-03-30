package com.himanshu.semhub.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


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


@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDay(): String {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()) // "EEEE" for full day name
    return today.format(formatter)
}