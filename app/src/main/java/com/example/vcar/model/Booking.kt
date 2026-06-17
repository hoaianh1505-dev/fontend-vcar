package com.example.vcar.model

data class Booking(
    val _id: String,
    val rentalDate: String,
    val status: String,
    val carId: Car?
)
