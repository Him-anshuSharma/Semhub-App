package com.himanshu.semhub.data.remote

import com.himanshu.semhub.data.model.Goal
import com.himanshu.semhub.data.model.OnboardingResponse
import com.himanshu.semhub.data.model.Task
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiService {
    // Existing onboard endpoint
    @Multipart
    @POST("v2/onboarding/onboard")
    suspend fun onboard(
        @Header("Authorization") authHeader: String,
        @Part images: List<MultipartBody.Part>,
        @Part audios: List<MultipartBody.Part>?
    ): OnboardingResponse

    // New endpoint for getting tasks
    @GET("v2/task/get-tasks")
    suspend fun getTasks(
        @Header("Authorization") authHeader: String
    ): List<Task>

    @GET("v2/goal/get-goals")
    suspend fun getGoals(
        @Header("Authorization") authHeader: String
    ): List<Goal>
}


