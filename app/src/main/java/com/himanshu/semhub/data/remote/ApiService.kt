package com.himanshu.semhub.data.remote

import com.himanshu.semhub.data.model.timetable.Timetable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("api/timetable/get-time-table/")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("id") id : RequestBody
    ): Response<Timetable>

    @Multipart
    @POST("api/chat/send-message/")
    suspend fun sendMessage(
        @Part("convo") conversation: RequestBody,
        @Part file: MultipartBody.Part? = null
    ): Response<String>
}