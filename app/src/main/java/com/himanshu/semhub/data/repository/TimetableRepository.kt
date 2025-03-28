package com.himanshu.semhub.data.repository

import com.himanshu.semhub.data.model.Timetable
import com.himanshu.semhub.data.remote.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class TimetableRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getTimeTable(file: File): Response<Timetable> {
        val mediaType = when{
            file.name.endsWith(".jpg") || file.name.endsWith(".jpeg") -> "image/jpeg"
            file.name.endsWith(".png") -> "image/png"
            else -> return Response.error(400, "Invalid file format".toResponseBody(null))
        }

        val requestBody = file.asRequestBody(mediaType.toMediaTypeOrNull())
        val multipartFile = MultipartBody.Part.createFormData("file", file.name, requestBody)

        return apiService.uploadFile(multipartFile)
    }
}