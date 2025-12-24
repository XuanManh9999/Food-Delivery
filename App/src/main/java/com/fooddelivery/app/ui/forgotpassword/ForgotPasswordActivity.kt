package com.fooddelivery.app.ui.forgotpassword

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fooddelivery.app.R
import com.fooddelivery.app.data.api.RetrofitClient
import com.fooddelivery.app.ui.auth.LoginActivity
import com.fooddelivery.app.ui.registration.RegistrationActivity
import com.fooddelivery.app.utils.UIUtils
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    
    private lateinit var emailEditText: TextInputEditText
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var sendLinkButton: android.widget.Button
    private lateinit var progressBar: ProgressBar
    private lateinit var rootView: View
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        
        // Get root view safely
        rootView = try {
            findViewById<View>(R.id.rightPanel) ?: window.decorView.rootView
        } catch (e: Exception) {
            window.decorView.rootView
        }
        
        // Initialize views
        emailEditText = findViewById(R.id.emailEditText)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        sendLinkButton = findViewById(R.id.sendLinkButton)
        progressBar = findViewById(R.id.progressBar)
        
        // Back button click
        try {
            findViewById<android.widget.Button>(R.id.backButton)?.setOnClickListener {
                finish()
            }
        } catch (e: Exception) {
            android.util.Log.e("ForgotPasswordActivity", "Error setting back button listener", e)
        }
        
        // Send link button click
        sendLinkButton.setOnClickListener {
            sendResetLink()
        }
        
        // Get Started button (left panel) - navigate to registration
        try {
            findViewById<android.widget.Button>(R.id.getStartedButton)?.setOnClickListener {
                val intent = Intent(this, RegistrationActivity::class.java)
                startActivity(intent)
            }
        } catch (e: Exception) {
            android.util.Log.e("ForgotPasswordActivity", "Error setting get started button listener", e)
        }
        
        // Clear error when user starts typing
        emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                clearError(emailInputLayout)
            }
        }
    }
    
    private fun sendResetLink() {
        val email = emailEditText.text?.toString()?.trim() ?: ""
        
        // Clear previous error
        clearError(emailInputLayout)
        
        // Validation
        if (TextUtils.isEmpty(email)) {
            setError(emailInputLayout, "Email không được để trống")
            emailEditText.requestFocus()
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setError(emailInputLayout, "Email không hợp lệ")
            emailEditText.requestFocus()
            return
        }
        
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val request = com.fooddelivery.app.data.api.ForgotPasswordRequest(email = email)
                val response = RetrofitClient.apiService.forgotPassword(request)
                
                if (response.isSuccessful) {
                    UIUtils.showSuccessSnackbar(rootView, "Link đổi mật khẩu đã được gửi đến email của bạn")
                    // Navigate back to login after delay
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2000)
                } else {
                    showLoading(false)
                    val errorMessage = when (response.code()) {
                        400 -> "Email không hợp lệ"
                        404 -> "Email không tồn tại trong hệ thống"
                        500 -> "Lỗi server, vui lòng thử lại sau"
                        else -> "Có lỗi xảy ra: ${response.message()}"
                    }
                    UIUtils.showErrorSnackbar(rootView, errorMessage)
                }
            } catch (e: java.net.UnknownHostException) {
                showLoading(false)
                UIUtils.showErrorSnackbar(rootView, "Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.")
            } catch (e: java.net.SocketTimeoutException) {
                showLoading(false)
                UIUtils.showErrorSnackbar(rootView, "Kết nối quá lâu. Vui lòng thử lại.")
            } catch (e: Exception) {
                showLoading(false)
                UIUtils.showErrorSnackbar(rootView, "Lỗi: ${e.message ?: "Có lỗi xảy ra"}")
                android.util.Log.e("ForgotPasswordActivity", "Send reset link error", e)
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        sendLinkButton.isEnabled = !show
        sendLinkButton.alpha = if (show) 0.6f else 1.0f
        emailEditText.isEnabled = !show
    }
    
    private fun setError(inputLayout: TextInputLayout, message: String) {
        inputLayout.error = message
        inputLayout.isErrorEnabled = true
    }
    
    private fun clearError(inputLayout: TextInputLayout) {
        inputLayout.error = null
        inputLayout.isErrorEnabled = false
    }
}
