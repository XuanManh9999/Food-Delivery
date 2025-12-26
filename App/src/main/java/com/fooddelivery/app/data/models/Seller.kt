package com.fooddelivery.app.data.models

import com.google.gson.annotations.SerializedName

data class SellerResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("store_name")
    val storeName: String,
    @SerializedName("store_address")
    val storeAddress: String,
    @SerializedName("store_phone")
    val storePhone: String?,
    @SerializedName("store_description")
    val storeDescription: String?,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("total_orders")
    val totalOrders: Int,
    @SerializedName("user")
    val user: SellerUser?
)

data class SellerUser(
    @SerializedName("id")
    val id: Int,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("phone_number")
    val phoneNumber: String
)

