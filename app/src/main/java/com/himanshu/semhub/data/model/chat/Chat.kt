package com.himanshu.semhub.data.model.chat

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson

@Entity(tableName = "messages")
@TypeConverters(MessageConverter::class)
data class Chat(
    @PrimaryKey
    val id: Int = 0,
    val conversation: List<Message> = emptyList()
)

data class Message (
    val content: String,
    val time: Long,
    val isUser: Boolean, //whether user received it or sent it
)

class MessageConverter {
    @TypeConverter
    fun fromMessageList(messages: List<Message>): String {
        return Gson().toJson(messages)
    }

    @TypeConverter
    fun toMessageList(json: String): List<Message> {
        return Gson().fromJson(json, Array<Message>::class.java).toList()
    }
}
