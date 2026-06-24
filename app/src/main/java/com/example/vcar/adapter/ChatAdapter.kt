package com.example.vcar.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vcar.R
import com.example.vcar.activities.CarDetailActivity
import com.example.vcar.model.Car
import com.example.vcar.model.ChatMessage

class ChatAdapter(
    private val messages: List<ChatMessage>
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layoutUser: View = itemView.findViewById(R.id.layoutUser)
        val txtUserMessage: TextView = itemView.findViewById(R.id.txtUserMessage)
        val layoutAI: View = itemView.findViewById(R.id.layoutAI)
        val txtAIMessage: TextView = itemView.findViewById(R.id.txtAIMessage)
        val rvRecommendedCars: RecyclerView = itemView.findViewById(R.id.rvRecommendedCars)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val msg = messages[position]

        if (msg.isUser) {
            holder.layoutUser.visibility = View.VISIBLE
            holder.layoutAI.visibility = View.GONE
            holder.txtUserMessage.text = msg.text
        } else {
            holder.layoutUser.visibility = View.GONE
            holder.layoutAI.visibility = View.VISIBLE
            holder.txtAIMessage.text = msg.text

            if (msg.recommendedCars.isNotEmpty()) {
                holder.rvRecommendedCars.visibility = View.VISIBLE
                holder.rvRecommendedCars.layoutManager = LinearLayoutManager(
                    holder.itemView.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                holder.rvRecommendedCars.adapter = RecommendedCarAdapter(msg.recommendedCars)
            } else {
                holder.rvRecommendedCars.visibility = View.GONE
            }
        }
    }

    // Nested adapter for horizontal listing inside chat bubble
    private class RecommendedCarAdapter(
        private val cars: List<Car>
    ) : RecyclerView.Adapter<RecommendedCarAdapter.RecCarViewHolder>() {

        class RecCarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imgRecCar: ImageView = view.findViewById(R.id.imgRecCar)
            val txtRecBrand: TextView = view.findViewById(R.id.txtRecBrand)
            val txtRecName: TextView = view.findViewById(R.id.txtRecName)
            val txtRecPrice: TextView = view.findViewById(R.id.txtRecPrice)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecCarViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recommended_car, parent, false)
            return RecCarViewHolder(view)
        }

        override fun getItemCount(): Int = cars.size

        override fun onBindViewHolder(holder: RecCarViewHolder, position: Int) {
            val car = cars[position]
            holder.txtRecName.text = car.name ?: "Chưa có tên"
            holder.txtRecBrand.text = car.brand?.uppercase() ?: "CHƯA RÕ"
            holder.txtRecPrice.text = "$${car.pricePerDay ?: 0.0} / ngày"

            val imageUrl = if (!car.images.isNullOrEmpty()) {
                "https://backend-vcar.onrender.com${car.images[0]}"
            } else {
                ""
            }

            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(holder.imgRecCar)

            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, CarDetailActivity::class.java).apply {
                    putExtra("carId", car._id)
                    putExtra("name", car.name)
                    putExtra("brand", car.brand)
                    putExtra("year", car.year ?: 0)
                    putExtra("price", car.pricePerDay ?: 0.0)
                    putExtra("description", car.description)
                    putExtra("location", car.location)
                    putExtra("available", car.available ?: false)
                    putExtra("image", imageUrl)
                }
                holder.itemView.context.startActivity(intent)
            }
        }
    }
}
