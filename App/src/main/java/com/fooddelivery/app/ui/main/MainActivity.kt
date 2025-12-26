package com.fooddelivery.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import com.fooddelivery.app.R
import com.fooddelivery.app.ui.auth.LoginActivity
import com.fooddelivery.app.ui.registration.RegistrationActivity
import com.fooddelivery.app.ui.customise.CustomisePizzaActivity
import com.fooddelivery.app.ui.main.adapters.Category
import com.fooddelivery.app.utils.UIUtils
import com.fooddelivery.app.ui.main.adapters.CategoryAdapter
import com.fooddelivery.app.ui.main.adapters.FoodItem
import com.fooddelivery.app.ui.main.adapters.FoodItemAdapter
import com.fooddelivery.app.ui.main.adapters.Restaurant
import com.fooddelivery.app.ui.main.adapters.RestaurantAdapter
import com.fooddelivery.app.ui.main.adapters.MenuCategoryAdapter
import com.fooddelivery.app.ui.main.adapters.MenuFoodAdapter
import com.fooddelivery.app.utils.PreferenceManager
import com.fooddelivery.app.data.repository.FoodRepository
import com.fooddelivery.app.data.models.FoodCategory
import com.fooddelivery.app.data.models.Food
import kotlinx.coroutines.launch
import android.widget.ProgressBar

class MainActivity : AppCompatActivity() {
    
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var foodItemsRecyclerView: RecyclerView
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var restaurantsRecyclerView: RecyclerView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuCategoriesRecyclerView: RecyclerView
    private lateinit var menuItemsRecyclerView: RecyclerView
    private val foodRepository = FoodRepository()
    private var currentSelectedCategory: FoodCategory? = null
    
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
            
            initializeViews()
            setupDrawerMenu()
            setupClickListeners()
            loadDataFromApi()
            
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error in onCreate", e)
            navigateToLogin()
        }
    }
    
    private fun initializeViews() {
        try {
            // Set location text
            findViewById<TextView>(R.id.locationText)?.text = "Lution Street, N4G-00...."
            
            // Load and display user info
            loadUserInfo()
            
            // Load and display cart total
            loadCartTotal()
            
            // Get RecyclerViews with null safety
            foodItemsRecyclerView = findViewById(R.id.foodItemsRecyclerView)
                ?: run {
                    android.util.Log.e("MainActivity", "foodItemsRecyclerView not found")
                    return
                }
            categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
                ?: run {
                    android.util.Log.e("MainActivity", "categoriesRecyclerView not found")
                    return
                }
            restaurantsRecyclerView = findViewById(R.id.restaurantsRecyclerView)
                ?: run {
                    android.util.Log.e("MainActivity", "restaurantsRecyclerView not found")
                    return
                }
            
            // Drawer menu views
            drawerLayout = findViewById(R.id.drawerLayout)
                ?: run {
                    android.util.Log.e("MainActivity", "drawerLayout not found")
                    return
                }
            menuCategoriesRecyclerView = findViewById(R.id.menuCategoriesRecyclerView)
                ?: run {
                    android.util.Log.e("MainActivity", "menuCategoriesRecyclerView not found")
                    return
                }
            menuItemsRecyclerView = findViewById(R.id.menuItemsRecyclerView)
                ?: run {
                    android.util.Log.e("MainActivity", "menuItemsRecyclerView not found")
                    return
                }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error initializing views: ${e.message}", e)
            // Don't throw, just log and continue with sample data
        }
    }
    
    private fun setupDrawerMenu() {
        lifecycleScope.launch {
            try {
                // Load categories from API
                val categoriesResult = foodRepository.getCategories()
                categoriesResult.onSuccess { categories ->
                    if (categories.isNotEmpty()) {
                        setupMenuCategories(categories)
                        // Load foods for first category
                        loadMenuFoods(categories[0])
                    }
                }.onFailure { error ->
                    android.util.Log.e("MainActivity", "Error loading menu categories: ${error.message}")
                }
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error setting up drawer menu: ${e.message}", e)
            }
        }
    }
    
    private fun setupMenuCategories(categories: List<FoodCategory>) {
        try {
            if (!::menuCategoriesRecyclerView.isInitialized) return
            
            val categoryAdapter = MenuCategoryAdapter(categories) { category ->
                currentSelectedCategory = category
                loadMenuFoods(category)
            }
            menuCategoriesRecyclerView.layoutManager = LinearLayoutManager(this)
            menuCategoriesRecyclerView.adapter = categoryAdapter
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error setting up menu categories: ${e.message}", e)
        }
    }
    
    private fun loadMenuFoods(category: FoodCategory) {
        lifecycleScope.launch {
            try {
                val foodsResult = foodRepository.getFoods(categoryId = category.id, isAvailable = true, limit = 50)
                foodsResult.onSuccess { foods ->
                    setupMenuFoods(foods)
                }.onFailure { error ->
                    android.util.Log.e("MainActivity", "Error loading menu foods: ${error.message}")
                    setupMenuFoods(emptyList())
                }
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error loading menu foods: ${e.message}", e)
                setupMenuFoods(emptyList())
            }
        }
    }
    
    private fun setupMenuFoods(foods: List<Food>) {
        try {
            if (!::menuItemsRecyclerView.isInitialized) return
            
            val foodAdapter = MenuFoodAdapter(foods) { food ->
                android.util.Log.d("MainActivity", "Menu food clicked: ${food.name}")
                // TODO: Open food detail screen
            }
            menuItemsRecyclerView.layoutManager = LinearLayoutManager(this)
            menuItemsRecyclerView.adapter = foodAdapter
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error setting up menu foods: ${e.message}", e)
        }
    }
    
    private fun loadDataFromApi() {
        // Show sample data first to avoid blank screen
        setupFoodItemsRecyclerView(getSampleFoodItems())
        setupCategoriesRecyclerView(getSampleCategories())
        setupRestaurantsRecyclerView(getSampleRestaurants())
        
        // Then load from API in background
        lifecycleScope.launch {
            try {
                // Load foods with timeout handling
                val foodsResult = foodRepository.getFoods(isAvailable = true, limit = 20)
                foodsResult.onSuccess { foods ->
                    val foodItems = foods.map { food ->
                        FoodItem(
                            id = food.id,
                            name = food.name,
                            restaurantName = "Restaurant", // Will be updated when we have seller info
                            restaurantLocation = "London",
                            restaurantType = "Restaurant",
                            discount = 0, // Calculate discount if needed
                            imageUrl = food.imageUrl
                        )
                    }
                    setupFoodItemsRecyclerView(foodItems)
                }.onFailure { error ->
                    android.util.Log.e("MainActivity", "Error loading foods: ${error.message}")
                    // Fallback to sample data
                    setupFoodItemsRecyclerView(getSampleFoodItems())
                }
                
                // Load categories
                val categoriesResult = foodRepository.getCategories()
                categoriesResult.onSuccess { categories ->
                    val categoryItems = categories.map { category ->
                        Category(
                            id = category.id,
                            name = category.name,
                            restaurantCount = 0, // Will be calculated if needed
                            imageUrl = null // Categories don't have images in API yet
                        )
                    }
                    setupCategoriesRecyclerView(categoryItems)
                }.onFailure { error ->
                    android.util.Log.e("MainActivity", "Error loading categories: ${error.message}")
                    // Fallback to sample data
                    setupCategoriesRecyclerView(getSampleCategories())
                }
                
                // Load sellers/restaurants
                val sellersResult = foodRepository.getSellers(limit = 20)
                sellersResult.onSuccess { sellers ->
                    val restaurantItems = sellers.mapIndexed { index, seller ->
                        Restaurant(
                            id = seller.id,
                            name = seller.storeName,
                            location = seller.storeAddress,
                            logoUrl = null, // Sellers don't have logo URLs yet
                            backgroundColor = getRestaurantColor(index)
                        )
                    }
                    setupRestaurantsRecyclerView(restaurantItems)
                }.onFailure { error ->
                    android.util.Log.e("MainActivity", "Error loading sellers: ${error.message}")
                    // Fallback to sample data
                    setupRestaurantsRecyclerView(getSampleRestaurants())
                }
                
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error loading data: ${e.message}", e)
                // Fallback to sample data on any error
                setupFoodItemsRecyclerView(getSampleFoodItems())
                setupCategoriesRecyclerView(getSampleCategories())
                setupRestaurantsRecyclerView(getSampleRestaurants())
            }
        }
    }
    
    private fun setupFoodItemsRecyclerView(foodItems: List<FoodItem>) {
        try {
            if (!::foodItemsRecyclerView.isInitialized) {
                android.util.Log.e("MainActivity", "foodItemsRecyclerView not initialized")
                return
            }
            val foodAdapter = FoodItemAdapter(foodItems) { item ->
                android.util.Log.d("MainActivity", "Food item clicked: ${item.name}")
                // Navigate to customise pizza activity
                val intent = Intent(this, CustomisePizzaActivity::class.java)
                intent.putExtra("food_name", item.name)
                intent.putExtra("food_image_url", item.imageUrl)
                intent.putExtra("food_price", 100000.0) // Default price, should come from API
                startActivity(intent)
            }
            foodItemsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            foodItemsRecyclerView.adapter = foodAdapter
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error setting up food items: ${e.message}", e)
        }
    }
    
    private fun setupCategoriesRecyclerView(categories: List<Category>) {
        try {
            if (!::categoriesRecyclerView.isInitialized) {
                android.util.Log.e("MainActivity", "categoriesRecyclerView not initialized")
                return
            }
            val categoryAdapter = CategoryAdapter(categories) { category ->
                android.util.Log.d("MainActivity", "Category clicked: ${category.name}")
            }
            categoriesRecyclerView.layoutManager = GridLayoutManager(this, 2)
            categoriesRecyclerView.adapter = categoryAdapter
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error setting up categories: ${e.message}", e)
        }
    }
    
    private fun setupRestaurantsRecyclerView(restaurants: List<Restaurant>) {
        try {
            if (!::restaurantsRecyclerView.isInitialized) {
                android.util.Log.e("MainActivity", "restaurantsRecyclerView not initialized")
                return
            }
            val restaurantAdapter = RestaurantAdapter(restaurants) { restaurant ->
                android.util.Log.d("MainActivity", "Restaurant clicked: ${restaurant.name}")
            }
            // Horizontal layout for restaurant cards
            restaurantsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            restaurantsRecyclerView.adapter = restaurantAdapter
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error setting up restaurants: ${e.message}", e)
        }
    }
    
    private fun getRestaurantColor(index: Int): Int {
        val colors = listOf(
            R.color.mcdonalds_red,
            R.color.papa_johns_green,
            R.color.kfc_red,
            R.color.primary,
            R.color.secondary
        )
        return colors[index % colors.size]
    }
    
    private fun setupClickListeners() {
        // User info panel click
        findViewById<View>(R.id.userInfoPanel)?.setOnClickListener {
            // TODO: Open profile screen
            android.util.Log.d("MainActivity", "User info clicked")
        }
        
        // Cart info panel click
        findViewById<View>(R.id.cartInfoPanel)?.setOnClickListener {
            // TODO: Open cart screen
            android.util.Log.d("MainActivity", "Cart info clicked")
        }
        
        // Menu click
        findViewById<ImageButton>(R.id.menuButton)?.setOnClickListener {
            if (::drawerLayout.isInitialized) {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        
        // Postcode search
        findViewById<ImageButton>(R.id.postcodeButton)?.setOnClickListener {
            val postcode = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.postcodeEditText)?.text?.toString()
            if (!postcode.isNullOrEmpty()) {
                // TODO: Search by postcode
                android.util.Log.d("MainActivity", "Search postcode: $postcode")
            }
        }
        
        // Filter button
        findViewById<com.google.android.material.button.MaterialButton>(R.id.filterButton)?.setOnClickListener {
            // TODO: Open filter dialog
            android.util.Log.d("MainActivity", "Filter clicked")
        }
        
        // Footer click listeners
        setupFooterListeners()
    }
    
    private fun setupFooterListeners() {
        // App Store button (in ordering banner)
        findViewById<android.widget.Button>(R.id.orderingAppStoreButton)?.setOnClickListener {
            // TODO: Open App Store
            android.util.Log.d("MainActivity", "Ordering App Store clicked")
        }

        // Google Play button (in ordering banner)
        findViewById<android.widget.Button>(R.id.orderingGooglePlayButton)?.setOnClickListener {
            // TODO: Open Google Play
            android.util.Log.d("MainActivity", "Ordering Google Play clicked")
        }
        
        // App Store button (in footer)
        findViewById<android.widget.Button>(R.id.appStoreButton)?.setOnClickListener {
            // TODO: Open App Store
            android.util.Log.d("MainActivity", "Footer App Store clicked")
        }

        // Google Play button (in footer)
        findViewById<android.widget.Button>(R.id.googlePlayButton)?.setOnClickListener {
            // TODO: Open Google Play
            android.util.Log.d("MainActivity", "Footer Google Play clicked")
        }
        
        // Business Get Started button
        findViewById<android.widget.Button>(R.id.businessGetStartedButton)?.setOnClickListener {
            // TODO: Navigate to business registration
            android.util.Log.d("MainActivity", "Business Get Started clicked")
            // Navigate to seller registration
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.putExtra("registration_type", "seller")
            startActivity(intent)
        }
        
        // Rider Get Started button
        findViewById<android.widget.Button>(R.id.riderGetStartedButton)?.setOnClickListener {
            // TODO: Navigate to rider registration
            android.util.Log.d("MainActivity", "Rider Get Started clicked")
            // Navigate to driver registration
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.putExtra("registration_type", "driver")
            startActivity(intent)
        }
        
        // How does Order.UK work? button
        findViewById<android.widget.Button>(R.id.howItWorksButton)?.setOnClickListener {
            // Scroll to How It Works section
            android.util.Log.d("MainActivity", "How It Works button clicked")
            findViewById<View>(R.id.howItWorksSection)?.let { section ->
                section.post {
                    val scrollView = findViewById<androidx.core.widget.NestedScrollView>(android.R.id.content)
                    scrollView?.smoothScrollTo(0, section.top)
                }
            }
        }
        
        // FAQ Questions
        findViewById<TextView>(R.id.faqQuestion1)?.setOnClickListener {
            // TODO: Show FAQ answer dialog
            android.util.Log.d("MainActivity", "FAQ Question 1 clicked")
            UIUtils.showInfoSnackbar(findViewById(R.id.knowMoreSection), "Payment methods: Credit/Debit cards, PayPal, Cash on delivery")
        }
        
        findViewById<TextView>(R.id.faqQuestion2)?.setOnClickListener {
            // TODO: Show FAQ answer dialog
            android.util.Log.d("MainActivity", "FAQ Question 2 clicked")
            UIUtils.showInfoSnackbar(findViewById(R.id.knowMoreSection), "Yes! You can track your order in real-time through the app")
        }
        
        findViewById<TextView>(R.id.faqQuestion3)?.setOnClickListener {
            // TODO: Show FAQ answer dialog
            android.util.Log.d("MainActivity", "FAQ Question 3 clicked")
            UIUtils.showInfoSnackbar(findViewById(R.id.knowMoreSection), "Yes! Check our promotions section for special discounts")
        }
        
        findViewById<TextView>(R.id.faqQuestion4)?.setOnClickListener {
            // TODO: Show FAQ answer dialog
            android.util.Log.d("MainActivity", "FAQ Question 4 clicked")
            UIUtils.showInfoSnackbar(findViewById(R.id.knowMoreSection), "Order.UK is available in most UK areas. Check your postcode!")
        }
        
        // Subscribe button
        findViewById<android.widget.Button>(R.id.subscribeButton)?.setOnClickListener {
            val email = findViewById<android.widget.EditText>(R.id.emailEditText)?.text?.toString()
            if (!email.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // TODO: Subscribe to newsletter
                android.util.Log.d("MainActivity", "Subscribe clicked: $email")
                UIUtils.showSuccessSnackbar(findViewById(R.id.footerLayout), "Đã đăng ký nhận email thành công!")
            } else {
                UIUtils.showErrorSnackbar(findViewById(R.id.footerLayout), "Vui lòng nhập email hợp lệ")
            }
        }
        
        // Email policy text with underline
        findViewById<TextView>(R.id.emailPolicyText)?.let { textView ->
            val fullText = "we wont spam, read our email policy"
            val spannable = android.text.SpannableString(fullText)
            val startIndex = fullText.indexOf("email policy")
            val endIndex = startIndex + "email policy".length
            spannable.setSpan(
                android.text.style.UnderlineSpan(),
                startIndex,
                endIndex,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textView.text = spannable
            textView.setOnClickListener {
                // TODO: Open email policy
                android.util.Log.d("MainActivity", "Email policy clicked")
            }
        }
        
        // Add underline to all footer links
        val footerLinks = listOf(
            R.id.termsLink,
            R.id.privacyLink,
            R.id.cookiesLink,
            R.id.modernSlaveryLink,
            R.id.getHelpLink,
            R.id.addRestaurantLink,
            R.id.signupDeliverLink,
            R.id.createBusinessLink
        )
        
        footerLinks.forEach { linkId ->
            findViewById<TextView>(linkId)?.let { textView ->
                val text = textView.text.toString()
                val spannable = android.text.SpannableString(text)
                spannable.setSpan(
                    android.text.style.UnderlineSpan(),
                    0,
                    text.length,
                    android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                textView.text = spannable
            }
        }
        
        // Social media buttons
        findViewById<android.widget.ImageButton>(R.id.facebookButton)?.setOnClickListener {
            // TODO: Open Facebook
            android.util.Log.d("MainActivity", "Facebook clicked")
        }
        
        findViewById<android.widget.ImageButton>(R.id.instagramButton)?.setOnClickListener {
            // TODO: Open Instagram
            android.util.Log.d("MainActivity", "Instagram clicked")
        }
        
        findViewById<android.widget.ImageButton>(R.id.tiktokButton)?.setOnClickListener {
            // TODO: Open TikTok
            android.util.Log.d("MainActivity", "TikTok clicked")
        }
        
        findViewById<android.widget.ImageButton>(R.id.snapchatButton)?.setOnClickListener {
            // TODO: Open Snapchat
            android.util.Log.d("MainActivity", "Snapchat clicked")
        }
        
        // Pages links
        findViewById<TextView>(R.id.termsLink)?.setOnClickListener {
            // TODO: Open Terms and Conditions
            android.util.Log.d("MainActivity", "Terms clicked")
        }
        
        findViewById<TextView>(R.id.privacyLink)?.setOnClickListener {
            // TODO: Open Privacy Policy
            android.util.Log.d("MainActivity", "Privacy clicked")
        }
        
        findViewById<TextView>(R.id.cookiesLink)?.setOnClickListener {
            // TODO: Open Cookies Policy
            android.util.Log.d("MainActivity", "Cookies clicked")
        }
        
        findViewById<TextView>(R.id.modernSlaveryLink)?.setOnClickListener {
            // TODO: Open Modern Slavery Statement
            android.util.Log.d("MainActivity", "Modern Slavery clicked")
        }
        
        // Links
        findViewById<TextView>(R.id.getHelpLink)?.setOnClickListener {
            // TODO: Open Help/Support
            android.util.Log.d("MainActivity", "Get Help clicked")
        }
        
        findViewById<TextView>(R.id.addRestaurantLink)?.setOnClickListener {
            // TODO: Open Add Restaurant form
            android.util.Log.d("MainActivity", "Add Restaurant clicked")
        }
        
        findViewById<TextView>(R.id.signupDeliverLink)?.setOnClickListener {
            // TODO: Open Sign up as Driver
            android.util.Log.d("MainActivity", "Sign up Deliver clicked")
        }
        
        findViewById<TextView>(R.id.createBusinessLink)?.setOnClickListener {
            // TODO: Open Create Business Account
            android.util.Log.d("MainActivity", "Create Business clicked")
        }
    }
    
    private fun getSampleFoodItems(): List<FoodItem> {
        return listOf(
            FoodItem(
                1, "Burgers & Fries", "Butterbrot Caf'e", "London", "Restaurant", 17,
                imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?w=400"
            ),
            FoodItem(
                2, "Grilled Meat Plate", "Grand Caf'e", "London", "Restaurant", 0,
                imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=400"
            ),
            FoodItem(
                3, "Burger & Sandwich", "Butterbrot", "London", "Restaurant", 15,
                imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?w=400"
            ),
            FoodItem(
                4, "Pasta Special", "Italian Corner", "London", "Restaurant", 20,
                imageUrl = "https://images.unsplash.com/photo-1551183053-bf91a1d81141?w=400"
            ),
            FoodItem(
                5, "Pizza Margherita", "Pizza Place", "London", "Restaurant", 12,
                imageUrl = "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400"
            )
        )
    }
    
    private fun getSampleCategories(): List<Category> {
        return listOf(
            Category(
                1, "Burgers & Fast food", 21,
                imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?w=400"
            ),
            Category(
                2, "Salads", 32,
                imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400"
            ),
            Category(
                3, "Pasta & Casuals", 4,
                imageUrl = "https://images.unsplash.com/photo-1551183053-bf91a1d81141?w=400"
            ),
            Category(
                4, "Pizza", 32,
                imageUrl = "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400"
            ),
            Category(
                5, "Breakfast", 4,
                imageUrl = "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=400"
            ),
            Category(
                6, "Soups", 32,
                imageUrl = "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400"
            )
        )
    }
    
    private fun getSampleRestaurants(): List<Restaurant> {
        return listOf(
            Restaurant(
                1, "McDonald's", "London",
                logoUrl = "https://logos-world.net/wp-content/uploads/2020/04/McDonalds-Logo.png",
                backgroundColor = R.color.mcdonalds_red
            ),
            Restaurant(
                2, "Papa Johns", "London",
                logoUrl = "https://logos-world.net/wp-content/uploads/2020/05/Papa-Johns-Logo.png",
                backgroundColor = R.color.papa_johns_green
            ),
            Restaurant(
                3, "KFC West", "London",
                logoUrl = "https://logos-world.net/wp-content/uploads/2020/04/KFC-Logo.png",
                backgroundColor = R.color.kfc_red
            ),
            Restaurant(
                4, "Burger King", "London",
                logoUrl = "https://logos-world.net/wp-content/uploads/2020/04/Burger-King-Logo.png",
                backgroundColor = R.color.primary
            ),
            Restaurant(
                5, "Subway", "London",
                logoUrl = "https://logos-world.net/wp-content/uploads/2020/04/Subway-Logo.png",
                backgroundColor = R.color.secondary
            )
        )
    }
    
    private fun loadUserInfo() {
        try {
            // Try to get full name first, then username
            val fullName = preferenceManager.getUserFullName()
            val username = preferenceManager.getUsername()
            val userNameText = findViewById<TextView>(R.id.userNameText)
            
            if (userNameText != null) {
                val displayName = when {
                    !fullName.isNullOrEmpty() -> {
                        // Use first name from full name
                        fullName.split(" ").firstOrNull() ?: fullName
                    }
                    !username.isNullOrEmpty() -> {
                        // Capitalize first letter of username
                        username.replaceFirstChar { 
                            if (it.isLowerCase()) it.titlecase() else it.toString() 
                        }
                    }
                    else -> "Guest"
                }
                userNameText.text = displayName
            }
            
            // Load user profile image if available
            // TODO: Load from API when user profile image is available
            val profileImage = findViewById<ImageView>(R.id.userProfileImage)
            profileImage?.setImageResource(R.drawable.placeholder_food)
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error loading user info: ${e.message}", e)
        }
    }
    
    private fun loadCartTotal() {
        lifecycleScope.launch {
            try {
                // Get cart total from API or local storage
                // For now, calculate from orders or use stored value
                val cartTotal = preferenceManager.getCartTotal()
                val cartTotalDisplay = findViewById<TextView>(R.id.cartTotalDisplay)
                if (cartTotalDisplay != null) {
                    if (cartTotal > 0.0) {
                        cartTotalDisplay.text = String.format("GBP %.2f", cartTotal)
                    } else {
                        cartTotalDisplay.text = "GBP 0.00"
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error loading cart total: ${e.message}", e)
            }
        }
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
