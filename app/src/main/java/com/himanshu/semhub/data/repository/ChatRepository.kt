package com.himanshu.semhub.data.repository

import android.util.Log
import com.himanshu.semhub.data.local.chat.ChatDao
import com.himanshu.semhub.data.model.chat.Chat
import com.himanshu.semhub.data.remote.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class ChatRepository @Inject constructor(private val apiService: ApiService, private val chatDao: ChatDao) {

    suspend fun sendMessage(message: String): String {
        val response = apiService.sendMessage(message.toRequestBody("application/json".toMediaType()))
        if (response.isSuccessful) {
            val responseData = response.body()
            if (responseData != null) {
               return responseData.toString()
            }
            else{
                throw Exception("Failed to send message")
            }
        }
        else{
            Log.d(TAG, "sendMessage: ${response.errorBody()}")
            throw Exception("Failed to send message")
        }
    }
    companion object{
        const val TAG = "ChatRepository"
    }

    suspend fun saveChat(chat: Chat) = chatDao.insertChat(chat)

    suspend fun getChat(): Chat? = chatDao.getChat()

}

