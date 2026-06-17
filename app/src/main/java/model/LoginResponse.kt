package model

import com.example.vcar.model.User

data class LoginResponse(
    val success: Boolean,
    val token: String,
    val user: User
)