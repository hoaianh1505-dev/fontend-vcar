package com.example.vcar.network

import com.example.vcar.model.BookingResponse
import com.example.vcar.model.CarsResponse
import com.example.vcar.model.LoginResponse
import com.example.vcar.model.ProfileResponse
import retrofit2.Call
import retrofit2.http.*

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String
)

data class BookingRequest(
    val carId: String,
    val rentalDate: String,
    val fullName: String,
    val phoneNumber: String,
    val licenseNumber: String,
    val address: String
)

data class AiRecommendRequest(
    val query: String
)

data class AiRecommendData(
    val recommendation: String,
    val recommendedCars: List<com.example.vcar.model.Car>
)

data class AiRecommendResponse(
    val success: Boolean,
    val data: AiRecommendData
)

interface ApiService {

    @POST("api/auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("api/auth/register")
    fun register(
        @Body request: RegisterRequest
    ): Call<LoginResponse>

    @GET("api/auth/profile")
    fun getProfile(
        @Header("Authorization")
        token: String
    ): Call<ProfileResponse>

    @GET("api/cars")
    fun getCars(): Call<CarsResponse>

    @POST("api/bookings")
    fun createBooking(
        @Header("Authorization")
        token: String,
        @Body request: BookingRequest
    ): Call<Void>

    @GET("api/bookings/my")
    fun getMyBookings(
        @Header("Authorization")
        token: String
    ): Call<BookingResponse>

    @POST("api/ai/recommend")
    fun getAiRecommendation(
        @Header("Authorization")
        token: String,
        @Body request: AiRecommendRequest
    ): Call<AiRecommendResponse>
}