package com.himanshu.semhub.data.remote

import com.himanshu.semhub.data.model.Goal
import com.himanshu.semhub.data.model.Task
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface ApiService {
    // Existing onboard endpoint
    @Multipart
    @POST("v2/onboarding/onboard")
    suspend fun onboard(
        @Header("Authorization") authHeader: String,
        @Part images: List<MultipartBody.Part>,
        @Part audios: List<MultipartBody.Part>?
    ): String

    // New get tasks endpoint
    @GET("v2/task/get-tasks")
    suspend fun getTasks(
        @Query("user_id") userId: String
    ): List<Task>

    // Get goals endpoint
    @GET("v2/goal/get-goals")
    suspend fun getGoals(
        @Query("user_id") userId: String
    ): List<Goal>
}


