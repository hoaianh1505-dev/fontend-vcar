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

        btnLogout.setOnClickListener {

            SharedPrefManager(this).logout()

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

            finish()
        }
    }

    private fun loadProfile() {

        val token =
            SharedPrefManager(this).getToken()

        RetrofitClient.api.getProfile(
            "Bearer $token"
        ).enqueue(object : Callback<ProfileResponse> {

            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {

                if (response.isSuccessful) {

                    response.body()?.let {

                        tvName.text = it.data.fullName
                        tvEmail.text = it.data.email
                        tvPhone.text = it.data.phone ?: ""
                        tvRole.text = it.data.role
                    }
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