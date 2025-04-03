package com.himanshu.semhub.data.local.chat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.himanshu.semhub.data.model.chat.Chat

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: Chat)

    @Query("SELECT * FROM messages LIMIT 1")
    suspend fun getChat(): Chat?

    @Query("DELETE FROM messages")
    suspend fun clearChat()

}