package com.fooddelivery.app.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fooddelivery.app.R
import com.fooddelivery.app.data.models.FoodCategory

class MenuCategoryAdapter(
    private val categories: List<FoodCategory>,
    private val onCategoryClick: (FoodCategory) -> Unit
) : RecyclerView.Adapter<MenuCategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val selectedIndicator: View = itemView.findViewById(R.id.selectedIndicator)
        val container: View = itemView.findViewById(R.id.categoryContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        
        holder.categoryName.text = category.name
        
        // Highlight selected category
        val isSelected = position == selectedPosition
        if (isSelected) {
            holder.container.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.menu_selected_bg)
            )
            holder.categoryName.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.menu_header_dark)
            )
            holder.categoryName.setTypeface(null, android.graphics.Typeface.BOLD)
            holder.selectedIndicator.visibility = View.VISIBLE
        } else {
            holder.container.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.background_light)
            )
            holder.categoryName.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.text_primary)
            )
            holder.categoryName.setTypeface(null, android.graphics.Typeface.NORMAL)
            holder.selectedIndicator.visibility = View.GONE
        }
        
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onCategoryClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}

