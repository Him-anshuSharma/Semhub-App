package com.himanshu.semhub.data.repository

import android.util.Log
import com.himanshu.semhub.data.model.Goal
import com.himanshu.semhub.data.model.Task
import com.himanshu.semhub.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) {
    private val TAG = "TaskRepository"

    /**
     * Fetches all tasks for the authenticated user
     * @return Flow emitting a Result containing the list of tasks
     */

}
