package com.himanshu.semhub.data.repository

import android.util.Log
import com.himanshu.semhub.data.model.OnboardingResponse
import com.himanshu.semhub.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingRepository @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) {
    private val TAG = "OnboardingRepository"

    /**
     * Performs user onboarding by sending images and audio files to the API
     * @param images List of image files to upload
     * @param audios Optional list of audio files to upload
     * @return Flow emitting the onboarding response
     */
    fun onboardUser(
        images: List<MultipartBody.Part>,
        audios: List<MultipartBody.Part>? = null
    ): Flow<Result<OnboardingResponse>> = flow {
        try {
            // Get fresh authentication token
            val authHeader = authRepository.getAuthorizationHeader()

            if (authHeader != null) {
                // Make API call with the token
                val response = apiService.onboard(authHeader, images, audios)
                emit(Result.success(response))

                // Save user ID from response if needed
                response.data.userId?.let { id ->
                    // You could save this to preferences or database
                    Log.d(TAG, "Onboarding successful for user ID: $id")
                }
            } else {
                // User is not authenticated
                emit(Result.failure(IllegalStateException("User not authenticated")))
                Unit
            }
        } catch (e: Exception) {
            Log.e(TAG, "Onboarding failed: ${e.message}")
            emit(Result.failure(e))
            Unit
        }
    }.flowOn(Dispatchers.IO)

    fun isUserOnboarded(): Flow<Boolean> = flow {
        val isOnboarded = false // Replace with actual implementation
        emit(isOnboarded)
    }.flowOn(Dispatchers.IO)
}
