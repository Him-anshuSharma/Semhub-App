package com.himanshu.semhub.data.repository
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.inject.Inject
import com.himanshu.semhub.data.local.AppDatabase
import com.himanshu.semhub.data.local.relations.GoalTaskCrossRef
import com.himanshu.semhub.data.model.Onboarding
import com.himanshu.semhub.data.remote.ApiService
import com.himanshu.semhub.utils.uriToFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody


class OnboardingRepository @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase,
    @ApplicationContext private val context: Context
) {

    val TAG = "OnboardingRepository"
    private val taskDao = appDatabase.taskDao()
    private val subtaskDao = appDatabase.subtaskDao()
    private val goalDao = appDatabase.goalDao()


    /**
     * Checks if there are existing tasks in the database
     * @return Result with Onboarding containing existing data if available, or null if no data exists
     */
    suspend fun checkExistingData(): Result<Onboarding?> = withContext(Dispatchers.IO) {
        try {
            val tasks = taskDao.getAllTasks()
            val goals = goalDao.getAllGoals()

            return@withContext if (tasks.isNotEmpty()) {
                // If tasks exist, create an Onboarding object with the existing data
                Result.success(Onboarding(tasks = tasks, goals = goals))
            } else {
                // No existing data
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking existing data: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Uploads images and optional audio files for user onboarding
     * and saves the received data to the local database
     *
     * @param token Authentication token
     * @param imageUris List of image URIs to upload
     * @param audioUris Optional list of audio URIs to upload
     * @return Onboarding containing tasks and goals for the user
     */
    suspend fun onboardUser(
        token: String,
        imageUris: List<Uri>,
        audioUris: List<Uri>? = null
    ): Result<Onboarding> {
        return try {
            val authHeader = "Bearer $token"
            // Convert image URIs to MultipartBody.Parts
            val imageParts = imageUris.mapNotNull { uri ->
                createMultipartBodyPart(uri, "images")
            }

            // Convert audio URIs to MultipartBody.Parts if provided
            val audioParts = audioUris?.mapNotNull { uri ->
                createMultipartBodyPart(uri, "audios")
            }

            // Make API call
            val response = apiService.onboard(authHeader, imageParts, audioParts)

            // Save the received data to the local database
            saveOnboardingDataToDb(response)

            Result.success(response)
        } catch (e: Exception) {
            Log.d(TAG, "Error during onboarding: ${e.message}")
            Result.failure(e)
        }

    }

    /**
     * Saves onboarding data (tasks, subtasks, goals) to the local database
     */
    private suspend fun saveOnboardingDataToDb(onboarding: Onboarding) = withContext(Dispatchers.IO) {
        // Save tasks and their subtasks
        onboarding.tasks.forEach { task ->
            // Ensure task has a priority to avoid NULL constraint error
            val taskWithPriority = if (task.priority.isNullOrEmpty()) {
                task.copy(priority = "medium")
            } else {
                task
            }

            // Insert the task and get its generated ID
            val taskId = taskDao.insert(taskWithPriority)

            // Insert subtasks with the parent task ID
            task.subtasks?.forEach { subtask ->
                val subtaskWithTaskId = subtask.copy(taskId = taskId)
                subtaskDao.insert(subtaskWithTaskId)
            }
            Log.d(TAG, "Saved task in DB: ${task.title}")
        }

        // Save goals and their relationships with tasks
        onboarding.goals?.forEach { goal ->
            // Insert the goal and get its generated ID
            val goalId = goalDao.insert(goal)

            // Create cross-references between goals and tasks
            goal.targetTasksTitles?.forEach { taskTitle ->
                if (taskTitle is String) {
                    // Find the task by title
                    val task = taskDao.findTaskByTitle(taskTitle)

                    if (task != null) {
                        // Create the cross-reference
                        goalDao.insertGoalTaskCrossRef(GoalTaskCrossRef(goalId, task.id))
                        Log.d(TAG, "Created goal-task relationship: ${goal.name} -> $taskTitle")
                    } else {
                        Log.w(TAG, "Could not find task with title: $taskTitle")
                    }
                }
            }
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
}
