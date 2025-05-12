package com.himanshu.semhub.data.repository
import android.content.Context
import android.net.Uri
import com.himanshu.semhub.data.remote.ApiService
import com.himanshu.semhub.data.remote.OnBoardingResponse
import com.himanshu.semhub.utils.uriToFile // Import your helper function
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class OnboardingRepository(
    private val apiService: ApiService,
    private val context: Context
) {
    /**
     * Uploads images and optional audio files for user onboarding
     *
     * @param token Authentication token
     * @param imageUris List of image URIs to upload
     * @param audioUris Optional list of audio URIs to upload
     * @return OnBoardingResponse containing tasks and goals for the user
     */
    suspend fun onboardUser(
        token: String,
        imageUris: List<Uri>,
        audioUris: List<Uri>? = null
    ): Result<OnBoardingResponse> = withContext(Dispatchers.IO) {
        try {
            // Convert image URIs to MultipartBody.Parts
            val imageParts = imageUris.mapNotNull { uri ->
                createMultipartBodyPart(uri, "images")
            }

            // Convert audio URIs to MultipartBody.Parts if provided
            val audioParts = audioUris?.mapNotNull { uri ->
                createMultipartBodyPart(uri, "audios")
            }

            // Make API call
            val response = apiService.onboard(token, imageParts, audioParts)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Creates a MultipartBody.Part from a URI using the existing uriToFile helper
     */
    private fun createMultipartBodyPart(uri: Uri, paramName: String): MultipartBody.Part? {
        // Use the existing helper function to convert URI to File
        val file = uriToFile(context, uri) ?: return null

        // Get MIME type
        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

        // Create MultipartBody.Part
        val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
            paramName,
            file.name,
            requestBody
        )
    }

    // Assuming your helper function is in a utils package, if not, you can include it here
    // fun uriToFile(context: Context, uri: Uri): File? { ... }
}
