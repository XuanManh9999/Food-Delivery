package com.fooddelivery.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fooddelivery.app.R
import com.fooddelivery.app.ui.auth.LoginActivity
import com.fooddelivery.app.utils.PreferenceManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var preferenceManager: PreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_main)
            
            preferenceManager = PreferenceManager(this)
            
            // Check if logged in
            if (!preferenceManager.isLoggedIn()) {
                navigateToLogin()
                return
            }
            
            // TODO: Set up main UI based on user role
            // - Buyer: Show food list, cart, orders
            // - Seller: Show food management, orders
            // - Driver: Show available orders, delivery status
            
            // Display user info
            val username = preferenceManager.getUsername() ?: "User"
            val role = preferenceManager.getUserRole() ?: "Unknown"
            findViewById<android.widget.TextView>(R.id.userInfoText)?.text = 
                "Xin chào, $username ($role)"
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error in onCreate", e)
            // If error, navigate to login
            navigateToLogin()
        }
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

