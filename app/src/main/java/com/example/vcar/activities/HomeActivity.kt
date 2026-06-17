package com.example.vcar.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
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

    private var masterCarsList: List<Car> = emptyList()

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

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCars(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        loadCars()
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
                        rvCars.adapter = CarAdapter(masterCarsList)
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

    private fun filterCars(query: String) {
        val filtered = if (query.isEmpty()) {
            masterCarsList
        } else {
            masterCarsList.filter {
                it.name?.contains(query, ignoreCase = true) == true || 
                it.brand?.contains(query, ignoreCase = true) == true ||
                it.location?.contains(query, ignoreCase = true) == true
            }
        }
        rvCars.adapter = CarAdapter(filtered)
    }
}
