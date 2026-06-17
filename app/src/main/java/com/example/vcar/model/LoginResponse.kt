package com.example.vcar.model

data class LoginData(
    val token: String,
    val user: User
)

data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val data: LoginData?
)
