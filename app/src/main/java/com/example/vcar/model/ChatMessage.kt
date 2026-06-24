package com.example.vcar.model

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val recommendedCars: List<Car> = emptyList()
)
