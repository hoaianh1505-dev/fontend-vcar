package com.example.vcar.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vcar.R
import com.example.vcar.model.ProfileResponse
import com.example.vcar.network.RetrofitClient
import com.example.vcar.utils.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvRole: TextView
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)
        tvRole = findViewById(R.id.tvRole)
        btnLogout = findViewById(R.id.btnLogout)

        loadProfile()

        findViewById<android.view.View>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        findViewById<android.view.View>(R.id.navHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        findViewById<android.view.View>(R.id.navContact).setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }

        btnLogout.setOnClickListener {

            SharedPrefManager(this).logout()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadProfile() {

        val token = SharedPrefManager(this).getToken()
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        RetrofitClient.api.getProfile(
            "Bearer $token"
        ).enqueue(object : Callback<ProfileResponse> {

            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        tvName.text = "Họ tên: ${it.data.fullName ?: "Chưa cập nhật"}"
                        tvEmail.text = "Email: ${it.data.email ?: "Chưa cập nhật"}"
                        tvPhone.text = "Số điện thoại: ${it.data.phone ?: "Chưa cập nhật"}"
                        tvRole.text = "Quyền hạn: ${it.data.role ?: "user"}"
                    }
                } else {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Không lấy được thông tin cá nhân",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<ProfileResponse>,
                t: Throwable
            ) {
                Toast.makeText(
                    this@ProfileActivity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
