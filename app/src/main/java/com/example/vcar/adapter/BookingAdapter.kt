package com.example.vcar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vcar.R
import com.example.vcar.model.Booking

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
        
        when (booking.status.lowercase()) {
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

        holder.btnContact.setOnClickListener {
            val context = holder.itemView.context
            val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                data = android.net.Uri.parse("tel:0363970865")
            }
            context.startActivity(intent)
        }
    }
}
