package com.fooddelivery.app.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fooddelivery.app.R

data class Restaurant(
    val id: Int,
    val name: String,
    val location: String,
    val logoUrl: String? = null,
    val backgroundColor: Int = R.color.primary
)

class RestaurantAdapter(
    private val restaurants: List<Restaurant>,
    private val onRestaurantClick: (Restaurant) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantLogo: ImageView = itemView.findViewById(R.id.restaurantLogo)
        val restaurantName: TextView = itemView.findViewById(R.id.restaurantName)
        val restaurantLocation: TextView = itemView.findViewById(R.id.restaurantLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurants[position]
        
        holder.restaurantName.text = restaurant.name
        
        // Show location if available
        if (restaurant.location.isNotEmpty()) {
            holder.restaurantLocation.text = restaurant.location
            holder.restaurantLocation.visibility = View.VISIBLE
        } else {
            holder.restaurantLocation.visibility = View.GONE
        }
        
        // Set background color based on restaurant
        holder.restaurantLogo.setBackgroundColor(
            holder.itemView.context.getColor(restaurant.backgroundColor)
        )
        
        // Load logo with Glide
        if (!restaurant.logoUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(restaurant.logoUrl)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.restaurantLogo)
        } else {
            holder.restaurantLogo.setImageResource(R.drawable.placeholder_food)
        }
        
        holder.itemView.setOnClickListener {
            onRestaurantClick(restaurant)
        }
    }

    override fun getItemCount(): Int = restaurants.size
}

