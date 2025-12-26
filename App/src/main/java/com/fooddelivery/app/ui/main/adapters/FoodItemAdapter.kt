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

data class FoodItem(
    val id: Int,
    val name: String,
    val restaurantName: String,
    val restaurantLocation: String,
    val restaurantType: String = "Restaurant",
    val discount: Int = 0,
    val imageUrl: String? = null
)

class FoodItemAdapter(
    private val items: List<FoodItem>,
    private val onItemClick: (FoodItem) -> Unit
) : RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder>() {

    class FoodItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodImage: ImageView = itemView.findViewById(R.id.foodImage)
        val discountBadge: TextView = itemView.findViewById(R.id.discountBadge)
        val restaurantType: TextView = itemView.findViewById(R.id.restaurantType)
        val restaurantName: TextView = itemView.findViewById(R.id.restaurantName)
        val restaurantLocation: TextView = itemView.findViewById(R.id.restaurantLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return FoodItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val item = items[position]
        
        holder.restaurantType.text = item.restaurantType
        holder.restaurantName.text = item.restaurantName
        holder.restaurantLocation.text = item.restaurantLocation
        
        if (item.discount > 0) {
            holder.discountBadge.text = "-${item.discount}%"
            holder.discountBadge.visibility = View.VISIBLE
        } else {
            holder.discountBadge.visibility = View.GONE
        }
        
        // Load image with Glide
        if (!item.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.foodImage)
        } else {
            holder.foodImage.setImageResource(R.drawable.placeholder_food)
        }
        
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}

