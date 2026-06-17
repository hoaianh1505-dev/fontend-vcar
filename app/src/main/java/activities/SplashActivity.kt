
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
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({

            val token = SharedPrefManager(this).getToken()

            if (!token.isNullOrEmpty()) {

                startActivity(
                    Intent(this, HomeActivity::class.java)
                )

            } else {

                startActivity(
                    Intent(this, LoginActivity::class.java)
                )

            }

            finish()

        }, 2000)
    }
}