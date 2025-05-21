package com.himanshu.semhub.data.model

import com.google.gson.annotations.SerializedName

data class OnboardingResponse(
    val success: Boolean,
    val message: String,
    val data: OnboardingData
)

data class OnboardingData(
    val goals: List<Goal>,
    @SerializedName("user_id")
    val userId: Int
)




