package com.fooddelivery.app.data.models

import com.google.gson.annotations.SerializedName

data class Food(
    @SerializedName("id")
    val id: Int,
    @SerializedName("seller_id")
    val sellerId: Int,
    @SerializedName("category_id")
    val categoryId: Int?,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("price")
    val price: Double,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("is_available")
    val isAvailable: Boolean,
    @SerializedName("stock_quantity")
    val stockQuantity: Int,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("total_orders")
    val totalOrders: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String?
)

data class FoodCategory(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("created_at")
    val createdAt: String
)

