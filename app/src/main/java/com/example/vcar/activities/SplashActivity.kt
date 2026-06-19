package com.example.vcar.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.vcar.R
import com.example.vcar.utils.SharedPrefManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)
        val token = SharedPrefManager(this).getToken()

        if (token != null) {
            // User is logged in, auto-redirect to Home screen after a short delay
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }, 1500)
        } else {
            // User is not logged in, show the Get Started button with a smooth fade-in animation
            btnGetStarted.visibility = View.VISIBLE
            btnGetStarted.alpha = 0f
            btnGetStarted.animate().alpha(1f).setDuration(800).start()

            btnGetStarted.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}
