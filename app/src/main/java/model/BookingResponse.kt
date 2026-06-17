package model

import com.example.vcar.model.Booking

data class BookingResponse(
    val success: Boolean,
    val data: List<Booking>
)