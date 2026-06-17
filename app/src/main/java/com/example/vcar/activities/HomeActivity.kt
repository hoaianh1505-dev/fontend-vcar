package com.example.vcar.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vcar.R
import com.example.vcar.adapter.CarAdapter
import com.example.vcar.model.Car
import com.example.vcar.model.CarsResponse
import com.example.vcar.network.RetrofitClient
import com.example.vcar.utils.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var rvCars: RecyclerView
    private lateinit var edtSearch: EditText
    
    private lateinit var chipAll: TextView
    private lateinit var chipElectric: TextView
    private lateinit var chipSport: TextView
    private lateinit var chipSedan: TextView
    private lateinit var chipSuv: TextView

    private var masterCarsList: List<Car> = emptyList()
    private var selectedCategory: String = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is logged in
        val token = SharedPrefManager(this).getToken()
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_home)

        rvCars = findViewById(R.id.rvCars)
        edtSearch = findViewById(R.id.edtSearch)

        rvCars.layoutManager = LinearLayoutManager(this)

        findViewById<android.view.View>(R.id.navHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        findViewById<android.view.View>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<android.view.View>(R.id.navContact).setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        setupCategoryChips()
        loadCars()
    }

    private fun setupCategoryChips() {
        chipAll = findViewById(R.id.chipAll)
        chipElectric = findViewById(R.id.chipElectric)
        chipSport = findViewById(R.id.chipSport)
        chipSedan = findViewById(R.id.chipSedan)
        chipSuv = findViewById(R.id.chipSuv)

        val clickListener = View.OnClickListener { v ->
            val cat = when (v.id) {
                R.id.chipAll -> "All"
                R.id.chipElectric -> "Electric"
                R.id.chipSport -> "Sport"
                R.id.chipSedan -> "Sedan"
                R.id.chipSuv -> "SUV"
                else -> "All"
            }
            updateCategorySelection(cat)
        }

        chipAll.setOnClickListener(clickListener)
        chipElectric.setOnClickListener(clickListener)
        chipSport.setOnClickListener(clickListener)
        chipSedan.setOnClickListener(clickListener)
        chipSuv.setOnClickListener(clickListener)
    }

    private fun updateCategorySelection(category: String) {
        selectedCategory = category

        // Reset all to inactive
        val chips = listOf(chipAll, chipElectric, chipSport, chipSedan, chipSuv)
        chips.forEach {
            it.setBackgroundResource(R.drawable.bg_chip_inactive)
            it.setTextColor(android.graphics.Color.parseColor("#8A8C9E"))
            it.setTypeface(null, android.graphics.Typeface.NORMAL)
        }

        // Set active
        val activeChip = when (category) {
            "All" -> chipAll
            "Electric" -> chipElectric
            "Sport" -> chipSport
            "Sedan" -> chipSedan
            "SUV" -> chipSuv
            else -> chipAll
        }
        activeChip.setBackgroundResource(R.drawable.bg_chip_active)
        activeChip.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        activeChip.setTypeface(null, android.graphics.Typeface.BOLD)

        applyFilters()
    }

    private fun loadCars() {
        RetrofitClient.api
            .getCars()
            .enqueue(object : Callback<CarsResponse> {

                override fun onResponse(
                    call: Call<CarsResponse>,
                    response: Response<CarsResponse>
                ) {
                    if (response.isSuccessful) {
                        masterCarsList = response.body()?.data ?: emptyList()
                        applyFilters()
                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "Không lấy được dữ liệu xe",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<CarsResponse>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@HomeActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun applyFilters() {
        val query = edtSearch.text.toString().trim()
        var filteredList = masterCarsList

        // 1. Filter by category
        if (selectedCategory != "All") {
            filteredList = filteredList.filter { car ->
                when (selectedCategory) {
                    "Electric" -> {
                        car.brand?.contains("Tesla", ignoreCase = true) == true ||
                        car.brand?.contains("VinFast", ignoreCase = true) == true
                    }
                    "Sport" -> {
                        car.brand?.contains("Porsche", ignoreCase = true) == true ||
                        car.brand?.contains("Chevrolet", ignoreCase = true) == true ||
                        (car.brand?.contains("Ford", ignoreCase = true) == true && car.name?.contains("Mustang", ignoreCase = true) == true)
                    }
                    "Sedan" -> {
                        val name = car.name ?: ""
                        val brand = car.brand ?: ""
                        name.contains("Civic", ignoreCase = true) ||
                        name.contains("Camry", ignoreCase = true) ||
                        brand.contains("Mazda", ignoreCase = true) ||
                        brand.contains("Vios", ignoreCase = true) ||
                        brand.contains("Accent", ignoreCase = true) ||
                        name.contains("A6", ignoreCase = true) ||
                        name.contains("530i", ignoreCase = true) ||
                        name.contains("C200", ignoreCase = true)
                    }
                    "SUV" -> {
                        val brand = car.brand ?: ""
                        val name = car.name ?: ""
                        brand.contains("Kia", ignoreCase = true) ||
                        (brand.contains("Toyota", ignoreCase = true) && !name.contains("Camry", ignoreCase = true) && !name.contains("Vios", ignoreCase = true)) ||
                        brand.contains("Mitsubishi", ignoreCase = true) ||
                        brand.contains("Land Rover", ignoreCase = true) ||
                        brand.contains("Lexus", ignoreCase = true) ||
                        brand.contains("Hyundai", ignoreCase = true) ||
                        (brand.contains("Ford", ignoreCase = true) && !name.contains("Mustang", ignoreCase = true))
                    }
                    else -> true
                }
            }
        }

        // 2. Filter by search query
        if (query.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.name?.contains(query, ignoreCase = true) == true ||
                it.brand?.contains(query, ignoreCase = true) == true ||
                it.location?.contains(query, ignoreCase = true) == true
            }
        }

        rvCars.adapter = CarAdapter(filteredList)
    }
}
