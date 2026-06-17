package com.example.vcar.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vcar.R
import com.example.vcar.model.BookingRequest
import com.example.vcar.network.RetrofitClient
import com.example.vcar.utils.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class BookingActivity : AppCompatActivity() {

    private lateinit var txtDate: TextView

    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val btnPickDate = findViewById<Button>(R.id.btnPickDate)
        val btnBooking = findViewById<Button>(R.id.btnBooking)

        txtDate = findViewById(R.id.txtDate)

        val carId = intent.getStringExtra("carId") ?: ""

        btnPickDate.setOnClickListener {
            showDatePicker()
        }

        btnBooking.setOnClickListener {

            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Chọn ngày thuê", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            bookingCar(carId)
        }
    }

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->

                selectedDate =
                    String.format(
                        "%04d-%02d-%02dT09:00:00.000Z",
                        year,
                        month + 1,
                        day
                    )

                txtDate.text = selectedDate

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun bookingCar(carId: String) {

        val token =
            SharedPrefManager(this).getToken()

        val request =
            BookingRequest(
                carId,
                selectedDate
            )

        RetrofitClient.instance.createBooking(
            "Bearer $token",
            request
        ).enqueue(object : Callback<Void> {

            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {

                if (response.isSuccessful) {

                    Toast.makeText(
                        this@BookingActivity,
                        "Đặt xe thành công",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    Toast.makeText(
                        this@BookingActivity,
                        "Đặt xe thất bại",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<Void>,
                t: Throwable
            ) {

                Toast.makeText(
                    this@BookingActivity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}