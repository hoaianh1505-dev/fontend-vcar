package com.example.vcar.model

data class CarsResponse(
    val success: Boolean,
    val count: Int,
    val data: List<Car>
)
