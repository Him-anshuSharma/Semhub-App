package com.himanshu.semhub.data.repository

import android.util.Log
import com.himanshu.semhub.data.localDb.dao.GoalDao
import com.himanshu.semhub.data.localDb.dao.GoalTaskCrossRefDao
import com.himanshu.semhub.data.localDb.dao.SubtaskDao
import com.himanshu.semhub.data.localDb.dao.TaskDao
import com.himanshu.semhub.data.localDb.relationships.GoalTaskCrossRef
import com.himanshu.semhub.data.mapper.GoalMapper
import com.himanshu.semhub.data.mapper.SubtaskMapper
import com.himanshu.semhub.data.mapper.TaskMapper
import com.himanshu.semhub.data.model.Goal
import com.himanshu.semhub.data.model.OnboardingResponse
import com.himanshu.semhub.data.model.Task
import com.himanshu.semhub.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingRepository @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository,
    private val taskDao: TaskDao,
    private val subtaskDao: SubtaskDao,
    private val goalDao: GoalDao,
    private val goalTaskCrossRefDao: GoalTaskCrossRefDao
) {

    suspend fun getToken(): String? {
        return authRepository.getAuthorizationHeader().toString()
    }

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

                Log.d(TAG, response.toString())

                // Save user ID from response if needed
                response.data.userId.let { id ->
                    Log.d(TAG, "Onboarding successful for user ID: $id")

                    // Fetch and save goals and tasks after successful onboarding
                    fetchAndSaveData(authHeader)
                }
            } else {
                // User is not authenticated
                emit(Result.failure(IllegalStateException("User not authenticated")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
            Log.e(TAG, "Onboarding failed: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)

    // In OnboardingRepository.kt
    private suspend fun fetchAndSaveData(authHeader: String) {
        withContext(Dispatchers.IO) {
            try {
                // Fetch and save tasks
                val tasks = apiService.getTasks(authHeader)

                // Convert API models to entities
                val taskEntities = tasks.map { TaskMapper.mapToEntity(it) }
                taskDao.insertTasks(taskEntities)

                // Fetch and save goals
                val goals = apiService.getGoals(authHeader)

                // Convert API models to entities
                val goalEntities = goals.map { GoalMapper.mapToEntity(it) }
                goalDao.insertGoals(goalEntities)

                // Save goal-task relationships
                val crossRefs = mutableListOf<GoalTaskCrossRef>()
                goals.forEach { goal ->
                    goal.id?.let { goalId ->
                        goal.targetTasks.forEach { taskId ->
                            crossRefs.add(
                                GoalTaskCrossRef(
                                    goalId = goalId,
                                    taskId = taskId.toInt()
                                )
                            )
                        }
                    }
                }
                goalTaskCrossRefDao.insertGoalTaskCrossRefs(crossRefs)

                //save subtasks
                for (task in tasks) {
                    val subtaskEntities = task.subtasks?.let {
                        it.map {
                            SubtaskMapper.mapToEntity(it, task.id!!)
                        }
                    }
                    if(subtaskEntities!=null) subtaskDao.insertSubtasks(subtaskEntities)
                }

                Log.d(TAG, "Successfully saved all data to local database")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching and saving data: ${e.message}")
            }
        }
    }


    fun getTasks(): Flow<Result<List<Task>>> = flow {
        try {
            val authHeader = authRepository.getAuthorizationHeader()

            if (authHeader != null) {
                val response = apiService.getTasks(authHeader)
                emit(Result.success(response))
                Log.d(TAG, "Successfully fetched ${response.size} tasks")
            } else {
                emit(Result.failure(IllegalStateException("User not authenticated")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
            Log.e(TAG, "Failed to get tasks: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)

    fun getGoals(): Flow<Result<List<Goal>>> = flow {
        try {
            val authHeader = authRepository.getAuthorizationHeader()

            if (authHeader != null) {
                val response = apiService.getGoals(authHeader)
                emit(Result.success(response))
                Log.d(TAG, "Successfully fetched ${response.size} goals")
            } else {
                emit(Result.failure(IllegalStateException("User not authenticated")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
            Log.e(TAG, "Failed to get goals: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)


    suspend fun isUserOnboarded(): Boolean {
        // Check if we have any tasks or goals in the database
        // If we do, the user has been onboarded
        val taskCount = taskDao.getTaskCount()
        return taskCount > 0

    }

    companion object{
        private const val TAG = "OnboardingRepository"
    }
}
