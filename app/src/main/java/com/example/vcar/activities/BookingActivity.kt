package com.example.vcar.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vcar.R
import com.example.vcar.network.BookingRequest
import com.example.vcar.network.RetrofitClient
import com.example.vcar.utils.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var btnPickDate: Button
    private lateinit var txtDate: TextView
    private lateinit var btnBooking: Button

    private var selectedDate: String = ""
    private var carId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        carId = intent.getStringExtra("carId")
        if (carId == null) {
            Toast.makeText(this, "Thông tin xe bị thiếu", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnPickDate = findViewById(R.id.btnPickDate)
        txtDate = findViewById(R.id.txtDate)
        btnBooking = findViewById(R.id.btnBooking)

        btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                txtDate.text = formattedDate
                selectedDate = formattedDate
            }, year, month, day)
            
            // Limit to current date onwards
            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()
        }

        btnBooking.setOnClickListener {
            performBooking()
        }
    }

    private fun performBooking() {
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày thuê xe", Toast.LENGTH_SHORT).show()
            return
        }

        val token = SharedPrefManager(this).getToken()
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        btnBooking.isEnabled = false
        val request = BookingRequest(carId!!, selectedDate)

        RetrofitClient.api.createBooking(
            "Bearer $token",
            request
        ).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                btnBooking.isEnabled = true
                if (response.isSuccessful) {
                    Toast.makeText(this@BookingActivity, "Đặt xe thành công!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@BookingActivity, "Không đặt được xe. Vui lòng thử lại", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                btnBooking.isEnabled = true
                Toast.makeText(this@BookingActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
