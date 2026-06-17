package com.example.vcar.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vcar.R
import com.example.vcar.adapter.BookingAdapter
import com.example.vcar.network.RetrofitClient
import com.example.vcar.utils.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView =
            findViewById(R.id.rvHistory)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        loadHistory()
    }

    private fun loadHistory() {

        val token =
            SharedPrefManager(this).getToken()

        RetrofitClient.instance.getMyBookings(
            "Bearer $token"
        ).enqueue(object : Callback<BookingResponse> {

            override fun onResponse(
                call: Call<BookingResponse>,
                response: Response<BookingResponse>
            ) {

                if (response.isSuccessful) {

                    val data =
                        response.body()?.data ?: emptyList()

                    recyclerView.adapter =
                        BookingAdapter(data)
                }
            }

            override fun onFailure(
                call: Call<BookingResponse>,
                t: Throwable
            ) {

            }
        })
    }
}