package com.fooddelivery.app.data.models

import com.google.gson.annotations.SerializedName

data class Payment(
    @SerializedName("id")
    val id: Int,
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("payment_number")
    val paymentNumber: String,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("status")
    val status: String,
    @SerializedName("transaction_id")
    val transactionId: String?,
    @SerializedName("payment_notes")
    val paymentNotes: String?,
    @SerializedName("paid_at")
    val paidAt: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String?
)

data class PaymentCreateRequest(
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("transaction_id")
    val transactionId: String? = null,
    @SerializedName("payment_notes")
    val paymentNotes: String? = null
)

