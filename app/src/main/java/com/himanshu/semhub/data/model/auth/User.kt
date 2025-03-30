package com.himanshu.semhub.data.model.auth

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null
)