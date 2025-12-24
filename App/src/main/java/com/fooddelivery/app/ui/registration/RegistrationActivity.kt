package com.fooddelivery.app.ui.registration

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
import com.fooddelivery.app.utils.UIUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {
    
    private var selectedUserType = "buyer" // buyer, seller, driver
    
    private lateinit var fullNameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var phoneNumberEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var storeNameEditText: TextInputEditText
    private lateinit var storeAddressEditText: TextInputEditText
    private lateinit var licenseNumberEditText: TextInputEditText
    private lateinit var addressEditText: TextInputEditText
    
    private lateinit var fullNameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var phoneNumberInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var storeNameInputLayout: TextInputLayout
    private lateinit var storeAddressInputLayout: TextInputLayout
    private lateinit var licenseNumberInputLayout: TextInputLayout
    private lateinit var addressInputLayout: TextInputLayout
    
    private lateinit var registerButton: android.widget.Button
    private lateinit var progressBar: ProgressBar
    private lateinit var rootView: View
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        
        // Get root view safely
        rootView = try {
            findViewById<View>(R.id.rightPanel) ?: window.decorView.rootView
        } catch (e: Exception) {
            window.decorView.rootView
        }
        
        // Initialize views
        fullNameEditText = findViewById(R.id.fullNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        storeNameEditText = findViewById(R.id.storeNameEditText)
        storeAddressEditText = findViewById(R.id.storeAddressEditText)
        licenseNumberEditText = findViewById(R.id.licenseNumberEditText)
        addressEditText = findViewById(R.id.addressEditText)
        
        fullNameInputLayout = findViewById(R.id.fullNameInputLayout)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        phoneNumberInputLayout = findViewById(R.id.phoneNumberInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout)
        storeNameInputLayout = findViewById(R.id.storeNameInputLayout)
        storeAddressInputLayout = findViewById(R.id.storeAddressInputLayout)
        licenseNumberInputLayout = findViewById(R.id.licenseNumberInputLayout)
        addressInputLayout = findViewById(R.id.addressInputLayout)
        
        registerButton = findViewById(R.id.registerButton)
        progressBar = findViewById(R.id.progressBar)
        
        // Setup tabs
        val userTypeTabs = findViewById<TabLayout>(R.id.userTypeTabs)
        
        userTypeTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        selectedUserType = "buyer"
                        storeNameInputLayout.visibility = View.GONE
                        storeAddressInputLayout.visibility = View.GONE
                        licenseNumberInputLayout.visibility = View.GONE
                        addressInputLayout.visibility = View.VISIBLE
                    }
                    1 -> {
                        selectedUserType = "seller"
                        storeNameInputLayout.visibility = View.VISIBLE
                        storeAddressInputLayout.visibility = View.VISIBLE
                        licenseNumberInputLayout.visibility = View.GONE
                        addressInputLayout.visibility = View.GONE
                    }
                    2 -> {
                        selectedUserType = "driver"
                        storeNameInputLayout.visibility = View.GONE
                        storeAddressInputLayout.visibility = View.GONE
                        licenseNumberInputLayout.visibility = View.VISIBLE
                        addressInputLayout.visibility = View.GONE
                    }
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        
        // Register button click
        registerButton.setOnClickListener {
            performRegistration()
        }
        
        // Login link click
        try {
            findViewById<View>(R.id.loginLink)?.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        } catch (e: Exception) {
            android.util.Log.e("RegistrationActivity", "Error setting login link listener", e)
        }
        
        // Clear errors when user starts typing
        setupErrorClearing()
    }
    
    private fun setupErrorClearing() {
        fullNameEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) clearError(fullNameInputLayout) }
        emailEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) clearError(emailInputLayout) }
        phoneNumberEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) clearError(phoneNumberInputLayout) }
        passwordEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) clearError(passwordInputLayout) }
        confirmPasswordEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) clearError(confirmPasswordInputLayout) }
        storeNameEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) clearError(storeNameInputLayout) }
        storeAddressEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) clearError(storeAddressInputLayout) }
        licenseNumberEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) clearError(licenseNumberInputLayout) }
        addressEditText.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) clearError(addressInputLayout) }
    }
    
    private fun performRegistration() {
        val fullName = fullNameEditText.text?.toString()?.trim() ?: ""
        val email = emailEditText.text?.toString()?.trim() ?: ""
        val phoneNumber = phoneNumberEditText.text?.toString()?.trim() ?: ""
        val password = passwordEditText.text?.toString() ?: ""
        val confirmPassword = confirmPasswordEditText.text?.toString() ?: ""
        
        // Clear all errors
        clearAllErrors()
        
        // Validation
        var hasError = false
        
        if (TextUtils.isEmpty(fullName)) {
            setError(fullNameInputLayout, "Họ và tên không được để trống")
            if (!hasError) fullNameEditText.requestFocus()
            hasError = true
        }
        
        if (TextUtils.isEmpty(email)) {
            setError(emailInputLayout, "Email không được để trống")
            if (!hasError) emailEditText.requestFocus()
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setError(emailInputLayout, "Email không hợp lệ")
            if (!hasError) emailEditText.requestFocus()
            hasError = true
        }
        
        if (TextUtils.isEmpty(phoneNumber)) {
            setError(phoneNumberInputLayout, "Số điện thoại không được để trống")
            if (!hasError) phoneNumberEditText.requestFocus()
            hasError = true
        } else if (phoneNumber.length < 10) {
            setError(phoneNumberInputLayout, "Số điện thoại phải có ít nhất 10 số")
            if (!hasError) phoneNumberEditText.requestFocus()
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
        
        if (password != confirmPassword) {
            setError(confirmPasswordInputLayout, "Mật khẩu không khớp")
            if (!hasError) confirmPasswordEditText.requestFocus()
            hasError = true
        }
        
        if (hasError) return
        
        // Generate username from email
        val username = email.split("@")[0]
        
        when (selectedUserType) {
            "buyer" -> {
                val address = addressEditText.text?.toString()?.trim()
                registerBuyer(email, username, password, fullName, phoneNumber, address)
            }
            "seller" -> {
                val storeName = storeNameEditText.text?.toString()?.trim() ?: ""
                val storeAddress = storeAddressEditText.text?.toString()?.trim() ?: ""
                if (TextUtils.isEmpty(storeName)) {
                    setError(storeNameInputLayout, "Tên cửa hàng không được để trống")
                    storeNameEditText.requestFocus()
                    return
                }
                if (TextUtils.isEmpty(storeAddress)) {
                    setError(storeAddressInputLayout, "Địa chỉ cửa hàng không được để trống")
                    storeAddressEditText.requestFocus()
                    return
                }
                registerSeller(email, username, password, fullName, phoneNumber, storeName, storeAddress)
            }
            "driver" -> {
                val licenseNumber = licenseNumberEditText.text?.toString()?.trim() ?: ""
                if (TextUtils.isEmpty(licenseNumber)) {
                    setError(licenseNumberInputLayout, "Số giấy phép lái xe không được để trống")
                    licenseNumberEditText.requestFocus()
                    return
                }
                registerDriver(email, username, password, fullName, phoneNumber, licenseNumber)
            }
        }
    }
    
    private fun registerBuyer(
        email: String,
        username: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        address: String?
    ) {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val request = com.fooddelivery.app.data.api.BuyerCreateRequest(
                    email = email,
                    username = username,
                    password = password,
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    address = address
                )
                val response = RetrofitClient.apiService.registerBuyer(request)
                if (response.isSuccessful) {
                    UIUtils.showSuccessSnackbar(rootView, "Đăng ký thành công!")
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 1000)
                } else {
                    showLoading(false)
                    val errorMessage = when (response.code()) {
                        400 -> "Thông tin không hợp lệ"
                        409 -> "Email hoặc username đã tồn tại"
                        500 -> "Lỗi server, vui lòng thử lại sau"
                        else -> "Đăng ký thất bại: ${response.message()}"
                    }
                    UIUtils.showErrorSnackbar(rootView, errorMessage)
                }
            } catch (e: java.net.UnknownHostException) {
                showLoading(false)
                UIUtils.showErrorSnackbar(rootView, "Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.")
            } catch (e: Exception) {
                showLoading(false)
                UIUtils.showErrorSnackbar(rootView, "Lỗi: ${e.message ?: "Có lỗi xảy ra"}")
                android.util.Log.e("RegistrationActivity", "Register error", e)
            }
        }
    }
    
    private fun registerSeller(
        email: String,
        username: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        storeName: String,
        storeAddress: String
    ) {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val request = com.fooddelivery.app.data.api.SellerCreateRequest(
                    email = email,
                    username = username,
                    password = password,
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    storeName = storeName,
                    storeAddress = storeAddress
                )
                val response = RetrofitClient.apiService.registerSeller(request)
                if (response.isSuccessful) {
                    UIUtils.showSuccessSnackbar(rootView, "Đăng ký thành công!")
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 1000)
                } else {
                    showLoading(false)
                    val errorMessage = when (response.code()) {
                        400 -> "Thông tin không hợp lệ"
                        409 -> "Email hoặc username đã tồn tại"
                        500 -> "Lỗi server, vui lòng thử lại sau"
                        else -> "Đăng ký thất bại: ${response.message()}"
                    }
                    UIUtils.showErrorSnackbar(rootView, errorMessage)
                }
            } catch (e: java.net.UnknownHostException) {
                showLoading(false)
                UIUtils.showErrorSnackbar(rootView, "Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.")
            } catch (e: Exception) {
                showLoading(false)
                UIUtils.showErrorSnackbar(rootView, "Lỗi: ${e.message ?: "Có lỗi xảy ra"}")
                android.util.Log.e("RegistrationActivity", "Register error", e)
            }
        }
    }
    
    private fun registerDriver(
        email: String,
        username: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        licenseNumber: String
    ) {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val request = com.fooddelivery.app.data.api.DriverCreateRequest(
                    email = email,
                    username = username,
                    password = password,
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    licenseNumber = licenseNumber
                )
                val response = RetrofitClient.apiService.registerDriver(request)
                if (response.isSuccessful) {
                    UIUtils.showSuccessSnackbar(rootView, "Đăng ký thành công!")
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 1000)
                } else {
                    showLoading(false)
                    val errorMessage = when (response.code()) {
                        400 -> "Thông tin không hợp lệ"
                        409 -> "Email hoặc username đã tồn tại"
                        500 -> "Lỗi server, vui lòng thử lại sau"
                        else -> "Đăng ký thất bại: ${response.message()}"
                    }
                    UIUtils.showErrorSnackbar(rootView, errorMessage)
                }
            } catch (e: java.net.UnknownHostException) {
                showLoading(false)
                UIUtils.showErrorSnackbar(rootView, "Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.")
            } catch (e: Exception) {
                showLoading(false)
                UIUtils.showErrorSnackbar(rootView, "Lỗi: ${e.message ?: "Có lỗi xảy ra"}")
                android.util.Log.e("RegistrationActivity", "Register error", e)
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        registerButton.isEnabled = !show
        registerButton.alpha = if (show) 0.6f else 1.0f
        fullNameEditText.isEnabled = !show
        emailEditText.isEnabled = !show
        phoneNumberEditText.isEnabled = !show
        passwordEditText.isEnabled = !show
        confirmPasswordEditText.isEnabled = !show
        storeNameEditText.isEnabled = !show
        storeAddressEditText.isEnabled = !show
        licenseNumberEditText.isEnabled = !show
        addressEditText.isEnabled = !show
    }
    
    private fun setError(inputLayout: TextInputLayout, message: String) {
        inputLayout.error = message
        inputLayout.isErrorEnabled = true
    }
    
    private fun clearError(inputLayout: TextInputLayout) {
        inputLayout.error = null
        inputLayout.isErrorEnabled = false
    }
    
    private fun clearAllErrors() {
        clearError(fullNameInputLayout)
        clearError(emailInputLayout)
        clearError(phoneNumberInputLayout)
        clearError(passwordInputLayout)
        clearError(confirmPasswordInputLayout)
        clearError(storeNameInputLayout)
        clearError(storeAddressInputLayout)
        clearError(licenseNumberInputLayout)
        clearError(addressInputLayout)
    }
}
