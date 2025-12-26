package com.fooddelivery.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fooddelivery.app.R
import com.fooddelivery.app.data.api.RetrofitClient
import com.fooddelivery.app.ui.forgotpassword.ForgotPasswordActivity
import com.fooddelivery.app.ui.main.MainActivity
import com.fooddelivery.app.ui.registration.RegistrationActivity
import com.fooddelivery.app.utils.PreferenceManager
import com.fooddelivery.app.utils.UIUtils
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var loginButton: android.widget.Button
    private lateinit var progressBar: ProgressBar
    private lateinit var rootView: View
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_login)
            
            // Get root view safely
            rootView = try {
                findViewById<View>(R.id.rightPanel) ?: window.decorView.rootView
            } catch (e: Exception) {
                android.util.Log.e("LoginActivity", "Error finding rightPanel", e)
                window.decorView.rootView
            }
            
            // Initialize PreferenceManager safely
            try {
                preferenceManager = PreferenceManager(this)
            } catch (e: Exception) {
                android.util.Log.e("LoginActivity", "Error initializing PreferenceManager", e)
                finish()
                return
            }
            
            // Check if already logged in
            try {
                if (preferenceManager.isLoggedIn()) {
                    navigateToMain()
                    return
                }
            } catch (e: Exception) {
                android.util.Log.e("LoginActivity", "Error checking login status", e)
                // Continue to login screen
            }
            
            // Initialize views with null safety
            try {
                emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
                    ?: throw IllegalStateException("emailEditText not found")
                passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
                    ?: throw IllegalStateException("passwordEditText not found")
                emailInputLayout = findViewById<TextInputLayout>(R.id.emailInputLayout)
                    ?: throw IllegalStateException("emailInputLayout not found")
                passwordInputLayout = findViewById<TextInputLayout>(R.id.passwordInputLayout)
                    ?: throw IllegalStateException("passwordInputLayout not found")
                loginButton = findViewById<android.widget.Button>(R.id.loginButton)
                    ?: throw IllegalStateException("loginButton not found")
                progressBar = findViewById<ProgressBar>(R.id.progressBar)
                    ?: throw IllegalStateException("progressBar not found")
            } catch (e: Exception) {
                android.util.Log.e("LoginActivity", "Error initializing views", e)
                try {
                    val safeRootView = if (::rootView.isInitialized) rootView else window.decorView.rootView
                    UIUtils.showErrorSnackbar(safeRootView, "Lỗi khởi tạo giao diện: ${e.message}")
                } catch (e2: Exception) {
                    android.util.Log.e("LoginActivity", "Error showing error snackbar", e2)
                }
                // Don't finish, let user see the error
            }
            
            // Login button click
            try {
                if (::loginButton.isInitialized) {
                    loginButton.setOnClickListener {
                        performLogin()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("LoginActivity", "Error setting login button listener", e)
            }
            
            // Register link click
            try {
                findViewById<android.widget.LinearLayout>(R.id.registerLink)?.setOnClickListener {
                    navigateToRegistration()
                }
            } catch (e: Exception) {
                android.util.Log.e("LoginActivity", "Error setting register link listener", e)
            }
            
            // Forgot password click
            try {
                findViewById<android.widget.TextView>(R.id.forgotPasswordText)?.setOnClickListener {
                    navigateToForgotPassword()
                }
            } catch (e: Exception) {
                android.util.Log.e("LoginActivity", "Error setting forgot password listener", e)
            }
            
            // Get Started button (left panel)
            try {
                findViewById<android.widget.Button>(R.id.getStartedButton)?.setOnClickListener {
                    navigateToRegistration()
                }
            } catch (e: Exception) {
                android.util.Log.e("LoginActivity", "Error setting get started button listener", e)
            }
            
            // Clear errors when user starts typing
            try {
                if (::emailEditText.isInitialized && ::emailInputLayout.isInitialized) {
                    emailEditText.setOnFocusChangeListener { _, hasFocus ->
                        if (hasFocus) {
                            clearError(emailInputLayout)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("LoginActivity", "Error setting email focus listener", e)
            }
            
            try {
                if (::passwordEditText.isInitialized && ::passwordInputLayout.isInitialized) {
                    passwordEditText.setOnFocusChangeListener { _, hasFocus ->
                        if (hasFocus) {
                            clearError(passwordInputLayout)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("LoginActivity", "Error setting password focus listener", e)
            }
        } catch (e: Exception) {
            android.util.Log.e("LoginActivity", "Error in onCreate", e)
            try {
                val safeRootView = if (::rootView.isInitialized) rootView else window.decorView.rootView
                UIUtils.showErrorSnackbar(safeRootView, "Lỗi khởi tạo: ${e.message}")
            } catch (e2: Exception) {
                android.util.Log.e("LoginActivity", "Error showing error message", e2)
                // Last resort: show toast
                try {
                    android.widget.Toast.makeText(this, "Lỗi khởi tạo ứng dụng", android.widget.Toast.LENGTH_LONG).show()
                } catch (e3: Exception) {
                    android.util.Log.e("LoginActivity", "Error showing toast", e3)
                }
            }
            // Don't finish immediately, let user see error or try again
            // finish()
        }
    }
    
    private fun performLogin() {
        val email = emailEditText.text?.toString()?.trim() ?: ""
        val password = passwordEditText.text?.toString() ?: ""
        
        // Clear previous errors
        clearError(emailInputLayout)
        clearError(passwordInputLayout)
        
        // Validation
        var hasError = false
        
        if (TextUtils.isEmpty(email)) {
            setError(emailInputLayout, "Email không được để trống")
            emailEditText.requestFocus()
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setError(emailInputLayout, "Email không hợp lệ")
            emailEditText.requestFocus()
            hasError = true
        }
        
        if (TextUtils.isEmpty(password)) {
            setError(passwordInputLayout, "Mật khẩu không được để trống")
            if (!hasError) passwordEditText.requestFocus()
            hasError = true
        } else if (password.length < 6) {
            setError(passwordInputLayout, "Mật khẩu phải có ít nhất 6 ký tự")
            if (!hasError) passwordEditText.requestFocus()
            hasError = true
        }
        
        if (hasError) return
        
        // Get username from email (or use email as username)
        val username = email.split("@")[0]
        
        login(username, password)
    }
    
    private fun login(username: String, password: String) {
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.login(username, password)
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        // Save token
                        preferenceManager.saveAccessToken(it.accessToken)
                        preferenceManager.setLoggedIn(true)
                        RetrofitClient.setAuthToken(it.accessToken)
                        
                        // Get user info
                        try {
                            val userResponse = RetrofitClient.apiService.getCurrentUser()
                            userResponse.body()?.let { user ->
                                preferenceManager.saveUserId(user.id)
                                preferenceManager.saveUsername(user.username)
                                preferenceManager.saveUserRole(user.role)
                                preferenceManager.saveUserFullName(user.fullName)
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("LoginActivity", "Failed to get user info: ${e.message}")
                        }
                        
                        UIUtils.showSuccessSnackbar(rootView, "Đăng nhập thành công!")
                        
                        // Navigate after short delay
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            navigateToMain()
                        }, 500)
                    } ?: run {
                        showLoading(false)
                        UIUtils.showErrorSnackbar(rootView, "Đăng nhập thất bại: Phản hồi không hợp lệ")
                    }
                } else {
                    showLoading(false)
                    val errorMessage = when (response.code()) {
                        401 -> "Email hoặc mật khẩu không đúng"
                        400 -> "Yêu cầu không hợp lệ"
                        500 -> "Lỗi server, vui lòng thử lại sau"
                        else -> "Đăng nhập thất bại: ${response.message()}"
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
                android.util.Log.e("LoginActivity", "Login error", e)
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        try {
            if (::progressBar.isInitialized) {
                progressBar.visibility = if (show) View.VISIBLE else View.GONE
            }
            if (::loginButton.isInitialized) {
                loginButton.isEnabled = !show
                loginButton.alpha = if (show) 0.6f else 1.0f
            }
            if (::emailEditText.isInitialized) {
                emailEditText.isEnabled = !show
            }
            if (::passwordEditText.isInitialized) {
                passwordEditText.isEnabled = !show
            }
        } catch (e: Exception) {
            android.util.Log.e("LoginActivity", "Error in showLoading", e)
        }
    }
    
    private fun setError(inputLayout: TextInputLayout, message: String) {
        inputLayout.error = message
        inputLayout.isErrorEnabled = true
    }
    
    private fun clearError(inputLayout: TextInputLayout) {
        inputLayout.error = null
        inputLayout.isErrorEnabled = false
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun navigateToRegistration() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }
    
    private fun navigateToForgotPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }
}
