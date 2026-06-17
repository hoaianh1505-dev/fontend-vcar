package com.example.vcar.network

import com.example.vcar.model.CarsResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("api/cars")
    fun getCars(): Call<CarsResponse>

}