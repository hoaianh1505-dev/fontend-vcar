package com.example.vcar.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.graphics.Color
import android.graphics.Typeface
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

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.vcar.model.ChatMessage
import com.example.vcar.adapter.ChatAdapter
import com.example.vcar.network.AiRecommendRequest
import com.example.vcar.network.AiRecommendResponse

class HomeActivity : AppCompatActivity() {

    private lateinit var rvCars: RecyclerView
    private lateinit var edtSearch: EditText
    
    private lateinit var chipAll: TextView
    private lateinit var chipAvailable: TextView
    private lateinit var chipVinfast: TextView
    private lateinit var chipMercedes: TextView
    private lateinit var chipBMW: TextView
    private lateinit var chipToyota: TextView
    
    private lateinit var fabAiChat: com.google.android.material.floatingactionbutton.FloatingActionButton

    private var masterCarsList: List<Car> = emptyList()
    private var selectedFilter: String = "All"

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
        
        chipAll = findViewById(R.id.chipAll)
        chipAvailable = findViewById(R.id.chipAvailable)
        chipVinfast = findViewById(R.id.chipVinfast)
        chipMercedes = findViewById(R.id.chipMercedes)
        chipBMW = findViewById(R.id.chipBMW)
        chipToyota = findViewById(R.id.chipToyota)
        
        fabAiChat = findViewById(R.id.fabAiChat)

        val chips = listOf(chipAll, chipAvailable, chipVinfast, chipMercedes, chipBMW, chipToyota)

        fun selectChip(selectedChip: TextView, filterValue: String) {
            selectedFilter = filterValue
            for (chip in chips) {
                if (chip == selectedChip) {
                    chip.setBackgroundResource(R.drawable.bg_filter_chip_selected)
                    chip.setTextColor(Color.WHITE)
                    chip.setTypeface(null, Typeface.BOLD)
                } else {
                    chip.setBackgroundResource(R.drawable.bg_filter_chip_unselected)
                    chip.setTextColor(Color.parseColor("#8A8C9E"))
                    chip.setTypeface(null, Typeface.NORMAL)
                }
            }
            applyFilters()
        }

        chipAll.setOnClickListener { selectChip(chipAll, "All") }
        chipAvailable.setOnClickListener { selectChip(chipAvailable, "Available") }
        chipVinfast.setOnClickListener { selectChip(chipVinfast, "VinFast") }
        chipMercedes.setOnClickListener { selectChip(chipMercedes, "Mercedes") }
        chipBMW.setOnClickListener { selectChip(chipBMW, "BMW") }
        chipToyota.setOnClickListener { selectChip(chipToyota, "Toyota") }

        fabAiChat.setOnClickListener {
            showAiChatDialog(token)
        }

        rvCars.layoutManager = LinearLayoutManager(this)

        findViewById<View>(R.id.navHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        findViewById<View>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<View>(R.id.navContact).setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }

        // Notification Bell Card interaction
        findViewById<View>(R.id.btnNotificationCard).setOnClickListener {
            findViewById<View>(R.id.notiDot).visibility = View.GONE
            Toast.makeText(this, "Không có thông báo mới nào", Toast.LENGTH_SHORT).show()
        }

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        loadCars()
    }

    private fun showAiChatDialog(token: String) {
        val bottomSheet = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_ai_chat, null)
        bottomSheet.setContentView(dialogView)

        // Set parent background to transparent to render custom rounded corners correctly
        (dialogView.parent as? View)?.setBackgroundColor(android.graphics.Color.TRANSPARENT)

        val rvChatMessages = dialogView.findViewById<RecyclerView>(R.id.rvChatMessages)
        val pbThinking = dialogView.findViewById<View>(R.id.pbThinking)
        val edtChatMessage = dialogView.findViewById<EditText>(R.id.edtChatMessage)
        val btnSendMessage = dialogView.findViewById<View>(R.id.btnSendMessage)
        val btnCloseChat = dialogView.findViewById<View>(R.id.btnCloseChat)

        val messagesList = mutableListOf<ChatMessage>()
        messagesList.add(ChatMessage("Xin chào! Tôi là trợ lý ảo VCar AI. Bạn cần thuê xe cho mục đích gì (như du lịch gia đình, hẹn hò lãng mạn hay xe cưới)? Hãy nói để tôi đề xuất nhé!", false))

        val chatAdapter = ChatAdapter(messagesList)
        rvChatMessages.adapter = chatAdapter
        rvChatMessages.layoutManager = LinearLayoutManager(this)

        btnCloseChat.setOnClickListener {
            bottomSheet.dismiss()
        }

        btnSendMessage.setOnClickListener {
            val query = edtChatMessage.text.toString().trim()
            if (query.isEmpty()) return@setOnClickListener

            // Add user message
            messagesList.add(ChatMessage(query, true))
            chatAdapter.notifyItemInserted(messagesList.size - 1)
            rvChatMessages.scrollToPosition(messagesList.size - 1)
            edtChatMessage.setText("")

            // Show loading and disable sending
            pbThinking.visibility = View.VISIBLE
            btnSendMessage.isEnabled = false

            RetrofitClient.api.getAiRecommendation("Bearer $token", AiRecommendRequest(query))
                .enqueue(object : Callback<AiRecommendResponse> {
                    override fun onResponse(call: Call<AiRecommendResponse>, response: Response<AiRecommendResponse>) {
                        pbThinking.visibility = View.GONE
                        btnSendMessage.isEnabled = true

                        if (response.isSuccessful && response.body() != null) {
                            val aiResponse = response.body()!!.data
                            messagesList.add(ChatMessage(aiResponse.recommendation, false, aiResponse.recommendedCars))
                        } else {
                            messagesList.add(ChatMessage("Rất tiếc, đã có sự cố xảy ra khi lấy gợi ý từ trợ lý AI. Vui lòng thử lại!", false))
                        }
                        chatAdapter.notifyItemInserted(messagesList.size - 1)
                        rvChatMessages.scrollToPosition(messagesList.size - 1)
                    }

                    override fun onFailure(call: Call<AiRecommendResponse>, t: Throwable) {
                        pbThinking.visibility = View.GONE
                        btnSendMessage.isEnabled = true
                        messagesList.add(ChatMessage("Không thể kết nối đến máy chủ AI: ${t.message}", false))
                        chatAdapter.notifyItemInserted(messagesList.size - 1)
                        rvChatMessages.scrollToPosition(messagesList.size - 1)
                    }
                })
        }

        bottomSheet.show()
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
        val searchQuery = edtSearch.text.toString().trim()
        var filteredList = masterCarsList

        // 1. Apply category/status/brand filter
        when (selectedFilter) {
            "Available" -> {
                filteredList = filteredList.filter { it.available == true }
            }
            "VinFast" -> {
                filteredList = filteredList.filter { it.brand?.contains("VinFast", ignoreCase = true) == true }
            }
            "Mercedes" -> {
                filteredList = filteredList.filter { it.brand?.contains("Mercedes", ignoreCase = true) == true }
            }
            "BMW" -> {
                filteredList = filteredList.filter { it.brand?.contains("BMW", ignoreCase = true) == true }
            }
            "Toyota" -> {
                filteredList = filteredList.filter { it.brand?.contains("Toyota", ignoreCase = true) == true }
            }
        }

        // 2. Apply search text query filter
        if (searchQuery.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.name?.contains(searchQuery, ignoreCase = true) == true ||
                it.brand?.contains(searchQuery, ignoreCase = true) == true ||
                it.location?.contains(searchQuery, ignoreCase = true) == true
            }
        }

        rvCars.adapter = CarAdapter(filteredList)
    }
}
