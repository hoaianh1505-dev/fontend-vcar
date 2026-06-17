package com.example.vcar.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vcar.R
import com.example.vcar.adapter.CarAdapter
import com.example.vcar.model.CarsResponse
import com.example.vcar.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var rvCars: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        rvCars = findViewById(R.id.rvCars)

        rvCars.layoutManager =
            LinearLayoutManager(this)

        loadCars()
    }

    private fun loadCars() {

        RetrofitClient.apiService
            .getCars()
            .enqueue(object : Callback<CarsResponse> {

                override fun onResponse(
                    call: Call<CarsResponse>,
                    response: Response<CarsResponse>
                ) {

                    if (response.isSuccessful) {

                        val cars =
                            response.body()?.data ?: emptyList()

                        rvCars.adapter =
                            CarAdapter(cars)

                    } else {

                        Toast.makeText(
                            this@HomeActivity,
                            "Không lấy được dữ liệu",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<CarsResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@HomeActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}