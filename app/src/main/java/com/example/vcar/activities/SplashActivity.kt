package com.example.vcar.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.vcar.R
import com.example.vcar.utils.SharedPrefManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("VCAR_SPLASH", "onCreate called")
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            android.util.Log.d("VCAR_SPLASH", "Handler triggered")
            checkLoginStatus()
        }, 2000) // 2 seconds delay
    }

    override fun onPause() {
        super.onPause()
        android.util.Log.d("VCAR_SPLASH", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        android.util.Log.d("VCAR_SPLASH", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        android.util.Log.d("VCAR_SPLASH", "onDestroy called")
    }

    private fun checkLoginStatus() {
        try {
            val token = SharedPrefManager(this).getToken()
            android.util.Log.d("VCAR_SPLASH", "Token found: ${token != null}")
            
            val nextActivity = if (token != null) {
                HomeActivity::class.java
            } else {
                LoginActivity::class.java
            }

            android.util.Log.d("VCAR_SPLASH", "Starting activity: ${nextActivity.simpleName}")
            startActivity(Intent(this, nextActivity))
            android.util.Log.d("VCAR_SPLASH", "startActivity returned")
            finish()
            android.util.Log.d("VCAR_SPLASH", "finish() called")
        } catch (e: Exception) {
            android.util.Log.e("VCAR_SPLASH", "Error in checkLoginStatus: ${e.message}", e)
            android.widget.Toast.makeText(this, "Lỗi khởi động: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
        }
    }
}

