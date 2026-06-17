package com.example.vcar.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vcar.R
import com.example.vcar.adapter.CarAdapter
import com.example.vcar.model.CarsResponse
import com.example.vcar.network.RetrofitClient
import com.example.vcar.utils.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var rvCars: RecyclerView
    private lateinit var imgAvatar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is logged in
        val token = SharedPrefManager(this).getToken()
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_home)

        rvCars = findViewById(R.id.rvCars)
        imgAvatar = findViewById(R.id.imgAvatar)

        rvCars.layoutManager = LinearLayoutManager(this)

        imgAvatar.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        loadCars()
    }

    private fun loadCars() {
        RetrofitClient.api
            .getCars()
            .enqueue(object : Callback<CarsResponse> {

                override fun onResponse(
                    call: Call<CarsResponse>,
                    response: Response<CarsResponse>
                ) {
                    if (response.isSuccessful) {
                        val cars = response.body()?.data ?: emptyList()
                        rvCars.adapter = CarAdapter(cars)
                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "Không lấy được dữ liệu xe",
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
