package com.himanshu.semhub.data.model.chat

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class Response(
    val mssg: String,
    val task: String? = null,
    @SerializedName("task content") val taskContent: Map<String, String> = emptyMap()
)