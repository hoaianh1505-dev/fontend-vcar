package com.example.vcar.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vcar.R
import com.example.vcar.network.BookingRequest
import com.example.vcar.network.RetrofitClient
import com.example.vcar.utils.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var btnPickDate: Button
    private lateinit var txtDate: TextView
    private lateinit var btnBooking: Button

    private var selectedDate: String = ""
    private var carId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        carId = intent.getStringExtra("carId")
        if (carId == null) {
            Toast.makeText(this, "Thông tin xe bị thiếu", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnPickDate = findViewById(R.id.btnPickDate)
        txtDate = findViewById(R.id.txtDate)
        btnBooking = findViewById(R.id.btnBooking)

        btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                txtDate.text = formattedDate
                selectedDate = formattedDate
            }, year, month, day)
            
            // Limit to current date onwards
            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()
        }

        btnBooking.setOnClickListener {
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ngày thuê xe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val termsMessage = """
                Chào mừng quý khách đến với dịch vụ thuê xe hạng sang Vcar. Vui lòng đọc kỹ các điều khoản dưới đây trước khi hoàn tất đặt xe:

                1. Điều kiện đối với người lái xe:
                - Có bằng lái xe hợp lệ tương thích với dòng xe đăng ký thuê.
                - Phải xuất trình CCCD/Hộ chiếu bản gốc khi ký hợp đồng bàn giao xe.

                2. Quy định an toàn và pháp lý:
                - Tuyệt đối không lái xe khi đã uống rượu bia hoặc sử dụng chất kích thích.
                - Không dùng xe vào mục đích phi pháp (vận chuyển hàng cấm, chở hàng lậu...).

                3. Trách nhiệm vật chất & giao thông:
                - Khách hàng tự chịu trách nhiệm chi trả toàn bộ các khoản phạt nguội, vi phạm giao thông phát sinh trong thời gian thuê xe.
                - Khách hàng tự bồi thường chi phí sửa chữa đối với các hư hỏng hoặc va quẹt do lỗi chủ quan của bản thân.

                4. Quy định nhận & trả xe:
                - Nhận và bàn giao xe đúng giờ, đúng địa điểm đã thỏa thuận.
                - Đảm bảo xe sạch sẽ và đầy đủ linh phụ kiện ban đầu khi hoàn trả.

                5. Xác nhận đặt xe qua Hotline (QUAN TRỌNG):
                - Sau khi gửi yêu cầu đặt xe thành công trên ứng dụng, quý khách bắt buộc phải thực hiện thêm 1 bước gọi trực tiếp đến số Hotline 0363970865 để nhân viên Vcar xác nhận thông tin và hoàn tất thủ tục đặt xe.

                6. Giới hạn thời gian thuê xe:
                - Ứng dụng này chỉ hỗ trợ đặt xe với giới hạn tối đa 1 ngày (24 giờ) cho mỗi lượt yêu cầu thuê.

                Bằng việc nhấn "Đồng ý và Đặt xe", quý khách cam kết đã đọc, hiểu rõ và đồng ý tuân thủ toàn bộ các điều khoản nêu trên.
            """.trimIndent()

            com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("ĐIỀU KHOẢN DỊCH VỤ THUÊ XE")
                .setMessage(termsMessage)
                .setPositiveButton("Đồng ý và Đặt xe") { dialog, _ ->
                    dialog.dismiss()
                    performBooking()
                }
                .setNegativeButton("Hủy bỏ") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun performBooking() {
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày thuê xe", Toast.LENGTH_SHORT).show()
            return
        }

        val token = SharedPrefManager(this).getToken()
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        btnBooking.isEnabled = false
        val request = BookingRequest(carId!!, selectedDate)

        RetrofitClient.api.createBooking(
            "Bearer $token",
            request
        ).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                btnBooking.isEnabled = true
                if (response.isSuccessful) {
                    Toast.makeText(this@BookingActivity, "Đặt xe thành công!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    val errorMsg = try {
                        val errorBodyString = response.errorBody()?.string()
                        if (!errorBodyString.isNullOrEmpty()) {
                            val json = org.json.JSONObject(errorBodyString)
                            json.optString("message", "Không đặt được xe. Vui lòng thử lại")
                        } else {
                            "Không đặt được xe. Vui lòng thử lại"
                        }
                    } catch (e: Exception) {
                        "Không đặt được xe. Vui lòng thử lại"
                    }
                    
                    com.google.android.material.dialog.MaterialAlertDialogBuilder(this@BookingActivity)
                        .setTitle("GIỚI HẠN THUÊ XE")
                        .setMessage(errorMsg)
                        .setPositiveButton("Đã hiểu") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                btnBooking.isEnabled = true
                com.google.android.material.dialog.MaterialAlertDialogBuilder(this@BookingActivity)
                    .setTitle("LỖI KẾT NỐI")
                    .setMessage("Không thể kết nối đến máy chủ: ${t.message}")
                    .setPositiveButton("Đóng") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
        })
    }
}
