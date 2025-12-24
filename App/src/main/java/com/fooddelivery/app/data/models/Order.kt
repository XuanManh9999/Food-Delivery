package com.fooddelivery.app.data.models

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id")
    val id: Int,
    @SerializedName("buyer_id")
    val buyerId: Int,
    @SerializedName("seller_id")
    val sellerId: Int,
    @SerializedName("driver_id")
    val driverId: Int?,
    @SerializedName("order_number")
    val orderNumber: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("subtotal")
    val subtotal: Double,
    @SerializedName("delivery_fee")
    val deliveryFee: Double,
    @SerializedName("total_amount")
    val totalAmount: Double,
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    @SerializedName("delivery_phone")
    val deliveryPhone: String,
    @SerializedName("delivery_notes")
    val deliveryNotes: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("delivered_at")
    val deliveredAt: String?,
    @SerializedName("items")
    val items: List<OrderItem>
)

data class OrderItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("food_id")
    val foodId: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unit_price")
    val unitPrice: Double,
    @SerializedName("subtotal")
    val subtotal: Double
)

data class OrderCreateRequest(
    @SerializedName("seller_id")
    val sellerId: Int,
    @SerializedName("items")
    val items: List<OrderItemCreate>,
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    @SerializedName("delivery_phone")
    val deliveryPhone: String,
    @SerializedName("delivery_notes")
    val deliveryNotes: String? = null,
    @SerializedName("delivery_fee")
    val deliveryFee: Double = 0.0
)

data class OrderItemCreate(
    @SerializedName("food_id")
    val foodId: Int,
    @SerializedName("quantity")
    val quantity: Int
)

