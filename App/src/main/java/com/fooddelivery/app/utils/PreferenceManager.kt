package com.fooddelivery.app.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "FoodDeliveryPrefs",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_CART_TOTAL = "cart_total"
        private const val KEY_USER_FULL_NAME = "user_full_name"
    }
    
    fun saveAccessToken(token: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }
    
    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }
    
    fun saveUserId(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }
    
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }
    
    fun saveUsername(username: String) {
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }
    
    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }
    
    fun saveUserRole(role: String) {
        prefs.edit().putString(KEY_USER_ROLE, role).apply()
    }
    
    fun getUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }
    
    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun saveUserFullName(fullName: String) {
        prefs.edit().putString(KEY_USER_FULL_NAME, fullName).apply()
    }
    
    fun getUserFullName(): String? {
        return prefs.getString(KEY_USER_FULL_NAME, null)
    }
    
    fun saveCartTotal(total: Double) {
        prefs.edit().putFloat(KEY_CART_TOTAL, total.toFloat()).apply()
    }
    
    fun getCartTotal(): Double {
        return prefs.getFloat(KEY_CART_TOTAL, 0f).toDouble()
    }
    
    fun clear() {
        prefs.edit().clear().apply()
    }
}

