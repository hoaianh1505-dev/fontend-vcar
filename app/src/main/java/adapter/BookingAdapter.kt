package adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vcar.R
import com.example.vcar.model.Booking

class BookingAdapter(
    private val list: List<Booking>
) : RecyclerView.Adapter<BookingAdapter.ViewHolder>() {

    class ViewHolder(itemView: View)
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
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_booking,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val booking = list[position]

        holder.txtCarName.text =
            booking.carId.name

        holder.txtBrand.text =
            booking.carId.brand

        holder.txtPrice.text =
            "$${booking.carId.pricePerDay}"

        holder.txtRentalDate.text =
            booking.rentalDate

        holder.txtStatus.text =
            booking.status

        when (booking.status) {

            "pending" ->
                holder.txtStatus.setTextColor(Color.YELLOW)

            "approved" ->
                holder.txtStatus.setTextColor(Color.GREEN)

            "cancelled" ->
                holder.txtStatus.setTextColor(Color.RED)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}