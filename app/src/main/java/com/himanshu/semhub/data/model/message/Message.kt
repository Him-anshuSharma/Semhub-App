package com.himanshu.semhub.data.model.message

import java.util.Date

data class Message (
    val content: String,
    val time: Date,
    val isUser: Boolean, //whether user received it or sent it
)