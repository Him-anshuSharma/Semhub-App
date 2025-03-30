package com.himanshu.semhub.data.repository

import android.util.Log
import com.himanshu.semhub.data.model.Timetable
import com.himanshu.semhub.data.remote.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class TimetableRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getTimeTable(file: File): Response<Timetable> {
        val mediaType = when {
            file.name.endsWith(".jpg") || file.name.endsWith(".jpeg") -> "image/jpeg"
            file.name.endsWith(".png") -> "image/png"
            else -> {
                Log.e("TimetableRepository", "Invalid file format: ${file.name}")
                val errorBody =
                    "Invalid file format".toResponseBody("text/plain".toMediaTypeOrNull())
                return Response.error(400, errorBody)
            }
        }

        val requestBody = file.asRequestBody(mediaType.toMediaTypeOrNull())
        val multipartFile = MultipartBody.Part.createFormData("file", file.name, requestBody)

        Log.d("TimetableRepository", "Calling API with file: ${file.name}")
        return apiService.uploadFile(multipartFile)
    }
}

