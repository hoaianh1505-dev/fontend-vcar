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
        val tvLoginLink = findViewById<android.widget.TextView>(R.id.tvLoginLink)
        val tvCopyright = findViewById<android.widget.TextView>(R.id.tvCopyright)
        val token = SharedPrefManager(this).getToken()

        if (token != null) {
            // User is logged in, auto-redirect to Home screen after a short delay
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }, 1500)
        } else {
            // Setup formatted text for the login link
            val linkHtmlText = "Bạn đã có tài khoản? <font color='#FFFFFF'><b>Đăng nhập</b></font>"
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tvLoginLink.text = android.text.Html.fromHtml(linkHtmlText, android.text.Html.FROM_HTML_MODE_LEGACY)
            } else {
                @Suppress("DEPRECATION")
                tvLoginLink.text = android.text.Html.fromHtml(linkHtmlText)
            }

            // User is not logged in, show and animate onboarding controls
            btnGetStarted.visibility = View.VISIBLE
            tvLoginLink.visibility = View.VISIBLE
            tvCopyright.visibility = View.VISIBLE

            btnGetStarted.alpha = 0f
            tvLoginLink.alpha = 0f
            tvCopyright.alpha = 0f

            btnGetStarted.animate().alpha(1f).setDuration(800).start()
            tvLoginLink.animate().alpha(1f).setDuration(800).start()
            tvCopyright.animate().alpha(1f).setDuration(800).start()

            val navigateToLogin = View.OnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            btnGetStarted.setOnClickListener(navigateToLogin)
            tvLoginLink.setOnClickListener(navigateToLogin)
        }
    }
}
