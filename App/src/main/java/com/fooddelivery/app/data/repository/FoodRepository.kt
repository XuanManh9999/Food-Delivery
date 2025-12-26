package com.fooddelivery.app.data.repository

import android.util.Log
import com.fooddelivery.app.data.api.ApiService
import com.fooddelivery.app.data.api.RetrofitClient
import com.fooddelivery.app.data.api.SellerResponse as ApiSellerResponse
import com.fooddelivery.app.data.models.Food
import com.fooddelivery.app.data.models.FoodCategory
import retrofit2.Response

class FoodRepository {
    private val apiService: ApiService = RetrofitClient.apiService
    
    suspend fun getFoods(
        sellerId: Int? = null,
        categoryId: Int? = null,
        isAvailable: Boolean? = null,
        skip: Int = 0,
        limit: Int = 100
    ): Result<List<Food>> {
        return try {
            val response = apiService.getFoods(sellerId, categoryId, isAvailable, skip, limit)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load foods: ${response.code()} ${response.message()}"))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("FoodRepository", "Timeout loading foods", e)
            Result.failure(Exception("Connection timeout. Please check your network."))
        } catch (e: java.net.UnknownHostException) {
            Log.e("FoodRepository", "Unknown host", e)
            Result.failure(Exception("Cannot connect to server. Please check if backend is running."))
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error loading foods", e)
            Result.failure(e)
        }
    }
    
    suspend fun getCategories(): Result<List<FoodCategory>> {
        return try {
            val response = apiService.getCategories()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load categories: ${response.code()} ${response.message()}"))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("FoodRepository", "Timeout loading categories", e)
            Result.failure(Exception("Connection timeout. Please check your network."))
        } catch (e: java.net.UnknownHostException) {
            Log.e("FoodRepository", "Unknown host", e)
            Result.failure(Exception("Cannot connect to server. Please check if backend is running."))
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error loading categories", e)
            Result.failure(e)
        }
    }
    
    suspend fun getFood(foodId: Int): Result<Food> {
        return try {
            val response = apiService.getFood(foodId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load food: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getSellers(
        skip: Int = 0,
        limit: Int = 100,
        search: String? = null,
        minRating: Double? = null
    ): Result<List<ApiSellerResponse>> {
        return try {
            val response = apiService.getSellers(skip, limit, search, minRating)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load sellers: ${response.code()} ${response.message()}"))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("FoodRepository", "Timeout loading sellers", e)
            Result.failure(Exception("Connection timeout. Please check your network."))
        } catch (e: java.net.UnknownHostException) {
            Log.e("FoodRepository", "Unknown host", e)
            Result.failure(Exception("Cannot connect to server. Please check if backend is running."))
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error loading sellers", e)
            Result.failure(e)
        }
    }
}

