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
import com.fooddelivery.app.data.models.Food
import java.text.NumberFormat
import java.util.Locale

class MenuFoodAdapter(
    private val foods: List<Food>,
    private val onFoodClick: (Food) -> Unit
) : RecyclerView.Adapter<MenuFoodAdapter.FoodViewHolder>() {

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodImage: ImageView = itemView.findViewById(R.id.foodImage)
        val foodName: TextView = itemView.findViewById(R.id.foodName)
        val foodDescription: TextView = itemView.findViewById(R.id.foodDescription)
        val foodPrice: TextView = itemView.findViewById(R.id.foodPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foods[position]
        
        holder.foodName.text = food.name
        
        // Show description if available
        if (!food.description.isNullOrEmpty()) {
            holder.foodDescription.text = food.description
            holder.foodDescription.visibility = View.VISIBLE
        } else {
            holder.foodDescription.visibility = View.GONE
        }
        
        // Format price
        val priceFormat = NumberFormat.getCurrencyInstance(Locale.UK)
        holder.foodPrice.text = priceFormat.format(food.price)
        
        // Load image with Glide
        if (!food.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(food.imageUrl)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.foodImage)
        } else {
            holder.foodImage.setImageResource(R.drawable.placeholder_food)
        }
        
        holder.itemView.setOnClickListener {
            onFoodClick(food)
        }
    }

    override fun getItemCount(): Int = foods.size
}

