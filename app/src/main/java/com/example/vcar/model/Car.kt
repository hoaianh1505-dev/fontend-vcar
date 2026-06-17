package com.example.vcar.model

data class Car(
    val _id: String?,
    val name: String?,
    val brand: String?,
    val year: Int?,
    val pricePerDay: Double?,
    val description: String?,
    val location: String?,
    val images: List<String>?,
    val available: Boolean?
)
