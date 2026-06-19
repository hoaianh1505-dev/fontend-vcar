package com.example.vcar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vcar.R
import com.example.vcar.model.Booking
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.Date

class BookingAdapter(
    private val bookings: List<Booking>
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val txtCarName: TextView =
            itemView.findViewById(R.id.txtCarName)

        val txtBrand: TextView =
            itemView.findViewById(R.id.txtBrand)

        val txtPrice: TextView =
            itemView.findViewById(R.id.txtPrice)

        val txtRentalDate: TextView =
            itemView.findViewById(R.id.txtRentalDate)

        val txtRemainingTime: TextView =
            itemView.findViewById(R.id.txtRemainingTime)

        val txtStatus: TextView =
            itemView.findViewById(R.id.txtStatus)

        val btnContact: View =
            itemView.findViewById(R.id.btnContact)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookingViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_booking,
                parent,
                false
            )

        return BookingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookings.size
    }

    override fun onBindViewHolder(
        holder: BookingViewHolder,
        position: Int
    ) {

        val booking = bookings[position]

        holder.txtCarName.text = booking.carId?.name ?: "N/A"
        holder.txtBrand.text = "Hãng: ${booking.carId?.brand ?: "N/A"}"
        holder.txtPrice.text = "Giá thuê: ${booking.carId?.pricePerDay ?: 0.0}$/ngày"
        
        // Clean date formatting if needed (takes first 10 characters "YYYY-MM-DD" from ISO string)
        val cleanDate = if (booking.rentalDate.length >= 10) booking.rentalDate.substring(0, 10) else booking.rentalDate
        holder.txtRentalDate.text = "Ngày thuê: $cleanDate"
        
        val statusLower = booking.status.lowercase()
        when (statusLower) {
            "approved" -> {
                holder.txtStatus.text = "Trạng thái: Đã duyệt"
                holder.txtStatus.setTextColor(android.graphics.Color.parseColor("#22C55E"))
            }
            "cancelled" -> {
                holder.txtStatus.text = "Trạng thái: Đã hủy"
                holder.txtStatus.setTextColor(android.graphics.Color.parseColor("#EF4444"))
            }
            else -> {
                holder.txtStatus.text = "Trạng thái: Đang chờ duyệt"
                holder.txtStatus.setTextColor(android.graphics.Color.parseColor("#F59E0B"))
            }
        }

        // Calculate and show remaining time
        if (statusLower == "cancelled") {
            holder.txtRemainingTime.visibility = View.GONE
        } else {
            val remainingText = getRemainingTimeText(booking.status, booking.rentalDate, booking.createdAt)
            if (remainingText.isNotEmpty() && remainingText != "N/A") {
                holder.txtRemainingTime.text = remainingText
                holder.txtRemainingTime.visibility = View.VISIBLE
                if (remainingText == "Đã hết hạn") {
                    holder.txtRemainingTime.setTextColor(android.graphics.Color.parseColor("#EF4444"))
                } else {
                    holder.txtRemainingTime.setTextColor(android.graphics.Color.parseColor("#F59E0B"))
                }
            } else {
                holder.txtRemainingTime.visibility = View.GONE
            }
        }

        holder.btnContact.setOnClickListener {
            val context = holder.itemView.context
            val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                data = android.net.Uri.parse("tel:0363970865")
            }
            context.startActivity(intent)
        }
    }

    private fun getRemainingTimeText(status: String, rentalDateStr: String, createdAtStr: String?): String {
        val utcZone = TimeZone.getTimeZone("UTC")
        
        // Date parsing configurations
        val isoParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
            timeZone = utcZone
        }
        
        val dayParser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = utcZone
        }

        val now = Date()

        val baseDate: Date? = if (status.lowercase() == "approved") {
            // For approved rentals, the rental date is when they rent it.
            try {
                isoParser.parse(rentalDateStr)
            } catch (e: Exception) {
                try {
                    dayParser.parse(rentalDateStr)
                } catch (e2: Exception) {
                    null
                }
            }
        } else {
            // For pending rentals, the timer tracks how long they have left to call and confirm (24h from creation)
            if (!createdAtStr.isNullOrEmpty()) {
                try {
                    isoParser.parse(createdAtStr)
                } catch (e: Exception) {
                    try {
                        dayParser.parse(createdAtStr)
                    } catch (e2: Exception) {
                        null
                    }
                }
            } else {
                null
            }
        }

        if (baseDate == null) return "N/A"

        // Expire exactly 24 hours (1 day) after base date
        val expirationTimeMs = baseDate.time + (24 * 60 * 60 * 1000)
        val remainingMs = expirationTimeMs - now.time

        if (remainingMs <= 0) {
            return "Đã hết hạn"
        }

        val hours = remainingMs / (60 * 60 * 1000)
        val minutes = (remainingMs % (60 * 60 * 1000)) / (60 * 1000)

        return if (status.lowercase() == "approved") {
            "Thời gian thuê còn: ${hours}h ${minutes}m"
        } else {
            "Hạn xác nhận còn: ${hours}h ${minutes}m"
        }
    }
}
