package com.example.vcar.activities

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.vcar.R

class CarDetailActivity : AppCompatActivity() {

    private lateinit var imgCar: ImageView
    private lateinit var txtName: TextView
    private lateinit var txtBrand: TextView
    private lateinit var txtYear: TextView
    private lateinit var txtPrice: TextView
    private lateinit var txtLocation: TextView
    private lateinit var txtDescription: TextView
    private lateinit var txtStatus: TextView
    private lateinit var btnBooking: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_car_detail)

        imgCar = findViewById(R.id.imgCar)
        txtName = findViewById(R.id.txtName)
        txtBrand = findViewById(R.id.txtBrand)
        txtYear = findViewById(R.id.txtYear)
        txtPrice = findViewById(R.id.txtPrice)
        txtLocation = findViewById(R.id.txtLocation)
        txtDescription = findViewById(R.id.txtDescription)
        txtStatus = findViewById(R.id.txtStatus)
        btnBooking = findViewById(R.id.btnBooking)

        val name = intent.getStringExtra("name")
        val brand = intent.getStringExtra("brand")
        val year = intent.getIntExtra("year", 0)
        val price = intent.getDoubleExtra("price", 0.0)
        val description = intent.getStringExtra("description")
        val location = intent.getStringExtra("location")
        val available = intent.getBooleanExtra("available", false)
        val image = intent.getStringExtra("image")

        txtName.text = name
        txtBrand.text = "Hãng: $brand"
        txtYear.text = "Năm sản xuất: $year"
        txtPrice.text = "Giá thuê: $price USD/ngày"
        txtLocation.text = "Địa điểm: $location"
        txtDescription.text = description

        txtStatus.text =
            if (available) "Còn xe"
            else "Hết xe"

        Glide.with(this)
            .load(image)
            .into(imgCar)
    }
}