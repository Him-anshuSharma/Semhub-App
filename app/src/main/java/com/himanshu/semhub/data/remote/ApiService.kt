package com.himanshu.semhub.data.remote

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @Multipart
    @POST("/v2/onboarding/onboard")
    suspend fun onboard(
        @Query("token") token: String,
        @Part images: List<MultipartBody.Part>,
        @Part audios: List<MultipartBody.Part>? = null
    ): OnBoardingResponse
}

// Response data class
data class OnBoardingResponse(
    val tasks: List<String>,
    val goals: List<String>
    // Add other fields as needed
)
