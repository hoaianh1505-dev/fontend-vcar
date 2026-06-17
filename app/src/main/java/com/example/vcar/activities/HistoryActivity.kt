package com.example.vcar.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vcar.R
import com.example.vcar.adapter.BookingAdapter
import com.example.vcar.model.BookingResponse
import com.example.vcar.network.RetrofitClient
import com.example.vcar.utils.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        rvHistory = findViewById(R.id.rvHistory)
        rvHistory.layoutManager = LinearLayoutManager(this)

        findViewById<android.view.View>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        findViewById<android.view.View>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        loadBookingHistory()
    }

    private fun loadBookingHistory() {
        val token = SharedPrefManager(this).getToken()
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        RetrofitClient.api.getMyBookings(
            "Bearer $token"
        ).enqueue(object : Callback<BookingResponse> {
            override fun onResponse(
                call: Call<BookingResponse>,
                response: Response<BookingResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val bookings = response.body()!!.data
                    rvHistory.adapter = BookingAdapter(bookings)
                } else {
                    Toast.makeText(
                        this@HistoryActivity,
                        "Không lấy được lịch sử đặt xe",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                Toast.makeText(
                    this@HistoryActivity,
                    "Lỗi kết nối: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
