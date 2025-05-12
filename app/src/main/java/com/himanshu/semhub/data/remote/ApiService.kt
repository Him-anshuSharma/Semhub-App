package com.himanshu.semhub.data.remote

import com.himanshu.semhub.data.model.Onboarding
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
    ): Onboarding
}

