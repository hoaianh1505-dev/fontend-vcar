package com.example.vcar.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.vcar.R

class ContactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        // Initialize Embedded Map WebView
        val mapWebView = findViewById<WebView>(R.id.mapWebView)
        mapWebView.settings.javaScriptEnabled = true
        mapWebView.webViewClient = WebViewClient()
        
        val mapHtml = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    html, body {
                        margin: 0;
                        padding: 0;
                        width: 100%;
                        height: 100%;
                        overflow: hidden;
                        background-color: #242533;
                    }
                    iframe {
                        width: 100%;
                        height: 100%;
                        border: 0;
                    }
                </style>
            </head>
            <body>
                <iframe 
                    src="https://maps.google.com/maps?q=123%20%C4%90%C6%B0%E1%BB%9Dng%203/2,%20Qu%E1%BA%ADn%2010,%20TP.%20H%E1%BB%93%20Ch%C3%AD%20Minh&t=&z=15&ie=UTF8&iwloc=&output=embed" 
                    allowfullscreen>
                </iframe>
            </body>
            </html>
        """.trimIndent()
        mapWebView.loadDataWithBaseURL("https://maps.google.com", mapHtml, "text/html", "UTF-8", null)

        // Bottom Navigation Listeners
        findViewById<View>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.navHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        // Google Map Click Intent (triggered by the floating button inside map container)
        findViewById<View>(R.id.cardGoogleMap).setOnClickListener {
            val query = "123 Đường 3/2, Quận 10, Hồ Chí Minh"
            val uri = Uri.parse("geo:0,0?q=" + Uri.encode(query))
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            mapIntent.setPackage("com.google.android.apps.maps")
            try {
                startActivity(mapIntent)
            } catch (e: Exception) {
                // Fallback to Web Browser
                val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query))
                val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
                startActivity(browserIntent)
            }
        }

        // Emergency SOS Click Intent
        findViewById<View>(R.id.btnEmergencySOS).setOnClickListener {
            val emergencyNumber = "0363970865" // Hotline 24/7
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$emergencyNumber")
            }
            startActivity(dialIntent)
        }

        // Quick Hotline Call Card
        findViewById<View>(R.id.cardPhone).setOnClickListener {
            val hotlineNumber = "0363970865"
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$hotlineNumber")
            }
            startActivity(dialIntent)
        }

        // Quick Email Intent Card
        findViewById<View>(R.id.cardEmail).setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:anhd78428@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Hỗ trợ dịch vụ VCar")
            }
            try {
                startActivity(emailIntent)
            } catch (e: Exception) {
                // Fallback if no email client installed
            }
        }

        // Showroom Address click opens Map
        findViewById<View>(R.id.cardAddress).setOnClickListener {
            findViewById<View>(R.id.cardGoogleMap).performClick()
        }
    }
}
