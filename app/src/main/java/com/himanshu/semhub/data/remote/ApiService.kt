package com.himanshu.semhub.data.remote

import Timetable
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("api/timetable/get-time-table/")
    suspend fun uploadFile(@Part file: MultipartBody.Part): Response<Timetable>
}