package com.example.vcar.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vcar.R
import com.example.vcar.activities.CarDetailActivity
import com.example.vcar.model.Car

class CarAdapter(
    private val cars: List<Car>
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    class CarViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val imgCar: ImageView =
            itemView.findViewById(R.id.imgCar)

        val txtName: TextView =
            itemView.findViewById(R.id.txtName)

        val txtBrand: TextView =
            itemView.findViewById(R.id.txtBrand)

        val txtPrice: TextView =
            itemView.findViewById(R.id.txtPrice)

        val txtLocation: TextView =
            itemView.findViewById(R.id.txtLocation)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_car,
                parent,
                false
            )

        return CarViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cars.size
    }

    override fun onBindViewHolder(
        holder: CarViewHolder,
        position: Int
    ) {

        val car = cars[position]

        holder.txtName.text = car.name
        holder.txtBrand.text = car.brand
        holder.txtPrice.text = "${car.pricePerDay}$/day"
        holder.txtLocation.text = car.location

        val imageUrl =
            if (car.images.isNotEmpty()) "https://backend-vcar.onrender.com${car.images[0]}"
            else ""

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.imgCar)

        holder.itemView.setOnClickListener {

            val intent = Intent(
                holder.itemView.context,
                CarDetailActivity::class.java
            )

            intent.putExtra("carId", car._id)
            intent.putExtra("name", car.name)
            intent.putExtra("brand", car.brand)
            intent.putExtra("year", car.year)
            intent.putExtra("price", car.pricePerDay)
            intent.putExtra("description", car.description)
            intent.putExtra("location", car.location)
            intent.putExtra("available", car.available)
            intent.putExtra("image", imageUrl)

            holder.itemView.context
                .startActivity(intent)
        }
    }
}
