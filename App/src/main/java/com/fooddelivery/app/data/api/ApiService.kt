package com.fooddelivery.app.data.api

import com.fooddelivery.app.data.models.*
    import retrofit2.Response
    import retrofit2.http.*

    interface ApiService {
    
    // Authentication
    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LoginResponse>
    
    @GET("api/auth/me")
    suspend fun getCurrentUser(): Response<User>
    
    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>
    
    // Registration
    @POST("api/register/seller")
    suspend fun registerSeller(@Body request: SellerCreateRequest): Response<SellerResponse>
    
    @POST("api/register/buyer")
    suspend fun registerBuyer(@Body request: BuyerCreateRequest): Response<BuyerResponse>
    
    @POST("api/register/driver")
    suspend fun registerDriver(@Body request: DriverCreateRequest): Response<DriverResponse>
    
    // Food
    @GET("api/foods")
    suspend fun getFoods(
        @Query("seller_id") sellerId: Int? = null,
        @Query("category_id") categoryId: Int? = null,
        @Query("is_available") isAvailable: Boolean? = null,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100
    ): Response<List<Food>>
    
    @GET("api/foods/{food_id}")
    suspend fun getFood(@Path("food_id") foodId: Int): Response<Food>
    
    @POST("api/foods")
    suspend fun createFood(@Body request: FoodCreateRequest): Response<Food>
    
    @PUT("api/foods/{food_id}")
    suspend fun updateFood(
        @Path("food_id") foodId: Int,
        @Body request: FoodUpdateRequest
    ): Response<Food>
    
    @DELETE("api/foods/{food_id}")
    suspend fun deleteFood(@Path("food_id") foodId: Int): Response<Unit>
    
    @GET("api/foods/categories")
    suspend fun getCategories(): Response<List<FoodCategory>>
    
    // Order
    @POST("api/orders")
    suspend fun createOrder(@Body request: OrderCreateRequest): Response<Order>
    
    @GET("api/orders")
    suspend fun getOrders(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100
    ): Response<List<Order>>
    
    @GET("api/orders/{order_id}")
    suspend fun getOrder(@Path("order_id") orderId: Int): Response<Order>
    
    @PATCH("api/orders/{order_id}/status")
    suspend fun updateOrderStatus(
        @Path("order_id") orderId: Int,
        @Body request: OrderStatusUpdateRequest
    ): Response<Order>
    
    // Payment
    @POST("api/payments")
    suspend fun createPayment(@Body request: PaymentCreateRequest): Response<Payment>
    
    @GET("api/payments")
    suspend fun getPayments(
        @Query("order_id") orderId: Int? = null,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100
    ): Response<List<Payment>>
    
    @GET("api/payments/{payment_id}")
    suspend fun getPayment(@Path("payment_id") paymentId: Int): Response<Payment>
    
    @PATCH("api/payments/{payment_id}/status")
    suspend fun updatePaymentStatus(
        @Path("payment_id") paymentId: Int,
        @Body request: PaymentStatusUpdateRequest
    ): Response<Payment>
}

// Request/Response models
data class LoginResponse(
    @com.google.gson.annotations.SerializedName("access_token")
    val accessToken: String,
    @com.google.gson.annotations.SerializedName("token_type")
    val tokenType: String
)

data class SellerCreateRequest(
    @com.google.gson.annotations.SerializedName("email")
    val email: String,
    @com.google.gson.annotations.SerializedName("username")
    val username: String,
    @com.google.gson.annotations.SerializedName("password")
    val password: String,
    @com.google.gson.annotations.SerializedName("full_name")
    val fullName: String,
    @com.google.gson.annotations.SerializedName("phone_number")
    val phoneNumber: String,
    @com.google.gson.annotations.SerializedName("store_name")
    val storeName: String,
    @com.google.gson.annotations.SerializedName("store_address")
    val storeAddress: String,
    @com.google.gson.annotations.SerializedName("store_phone")
    val storePhone: String? = null,
    @com.google.gson.annotations.SerializedName("store_description")
    val storeDescription: String? = null,
    @com.google.gson.annotations.SerializedName("license_number")
    val licenseNumber: String? = null
)

data class BuyerCreateRequest(
    @com.google.gson.annotations.SerializedName("email")
    val email: String,
    @com.google.gson.annotations.SerializedName("username")
    val username: String,
    @com.google.gson.annotations.SerializedName("password")
    val password: String,
    @com.google.gson.annotations.SerializedName("full_name")
    val fullName: String,
    @com.google.gson.annotations.SerializedName("phone_number")
    val phoneNumber: String,
    @com.google.gson.annotations.SerializedName("address")
    val address: String? = null
)

data class DriverCreateRequest(
    @com.google.gson.annotations.SerializedName("email")
    val email: String,
    @com.google.gson.annotations.SerializedName("username")
    val username: String,
    @com.google.gson.annotations.SerializedName("password")
    val password: String,
    @com.google.gson.annotations.SerializedName("full_name")
    val fullName: String,
    @com.google.gson.annotations.SerializedName("phone_number")
    val phoneNumber: String,
    @com.google.gson.annotations.SerializedName("license_number")
    val licenseNumber: String,
    @com.google.gson.annotations.SerializedName("vehicle_type")
    val vehicleType: String? = null,
    @com.google.gson.annotations.SerializedName("vehicle_number")
    val vehicleNumber: String? = null
)

data class SellerResponse(
    @com.google.gson.annotations.SerializedName("id")
    val id: Int,
    @com.google.gson.annotations.SerializedName("user_id")
    val userId: Int,
    @com.google.gson.annotations.SerializedName("store_name")
    val storeName: String,
    @com.google.gson.annotations.SerializedName("user")
    val user: User
)

data class BuyerResponse(
    @com.google.gson.annotations.SerializedName("id")
    val id: Int,
    @com.google.gson.annotations.SerializedName("user_id")
    val userId: Int,
    @com.google.gson.annotations.SerializedName("user")
    val user: User
)

data class DriverResponse(
    @com.google.gson.annotations.SerializedName("id")
    val id: Int,
    @com.google.gson.annotations.SerializedName("user_id")
    val userId: Int,
    @com.google.gson.annotations.SerializedName("license_number")
    val licenseNumber: String,
    @com.google.gson.annotations.SerializedName("user")
    val user: User
)

data class FoodCreateRequest(
    @com.google.gson.annotations.SerializedName("name")
    val name: String,
    @com.google.gson.annotations.SerializedName("description")
    val description: String? = null,
    @com.google.gson.annotations.SerializedName("price")
    val price: Double,
    @com.google.gson.annotations.SerializedName("image_url")
    val imageUrl: String? = null,
    @com.google.gson.annotations.SerializedName("category_id")
    val categoryId: Int? = null,
    @com.google.gson.annotations.SerializedName("stock_quantity")
    val stockQuantity: Int = 0
)

data class FoodUpdateRequest(
    @com.google.gson.annotations.SerializedName("name")
    val name: String? = null,
    @com.google.gson.annotations.SerializedName("description")
    val description: String? = null,
    @com.google.gson.annotations.SerializedName("price")
    val price: Double? = null,
    @com.google.gson.annotations.SerializedName("image_url")
    val imageUrl: String? = null,
    @com.google.gson.annotations.SerializedName("category_id")
    val categoryId: Int? = null,
    @com.google.gson.annotations.SerializedName("is_available")
    val isAvailable: Boolean? = null,
    @com.google.gson.annotations.SerializedName("stock_quantity")
    val stockQuantity: Int? = null
)

data class OrderStatusUpdateRequest(
    @com.google.gson.annotations.SerializedName("status")
    val status: String
)

data class PaymentStatusUpdateRequest(
    @com.google.gson.annotations.SerializedName("status")
    val status: String,
    @com.google.gson.annotations.SerializedName("transaction_id")
    val transactionId: String? = null
)

data class ForgotPasswordRequest(
    @com.google.gson.annotations.SerializedName("email")
    val email: String
)

data class ForgotPasswordResponse(
    @com.google.gson.annotations.SerializedName("message")
    val message: String,
    @com.google.gson.annotations.SerializedName("status")
    val status: String
)

