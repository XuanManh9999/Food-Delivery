package com.fooddelivery.app.ui.customise

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fooddelivery.app.R
import com.bumptech.glide.Glide

class CustomisePizzaActivity : AppCompatActivity() {

    private lateinit var toppingsRecyclerView: RecyclerView
    private lateinit var totalPriceText: TextView
    private lateinit var nextStepButton: Button
    
    private val selectedToppings = mutableSetOf<String>()
    private val maxFreeToppings = 4
    private var basePrice = 100000.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customise_pizza)

        // Get food item data from intent
        val foodName = intent.getStringExtra("food_name") ?: "Pizza"
        val foodImageUrl = intent.getStringExtra("food_image_url")
        val foodPrice = intent.getDoubleExtra("food_price", 100000.0)
        
        basePrice = foodPrice

        initializeViews()
        setupClickListeners()
        setupToppingsRecyclerView()
        updateTotalPrice()
    }

    private fun initializeViews() {
        // Close button
        findViewById<ImageButton>(R.id.closeButton)?.setOnClickListener {
            finish()
        }

        // Pizza image
        val pizzaImage = findViewById<ImageView>(R.id.pizzaImage)
        val imageUrl = intent.getStringExtra("food_image_url")
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .into(pizzaImage)
        }

        // Pizza preview
        val pizzaPreview = findViewById<ImageView>(R.id.pizzaPreview)
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .into(pizzaPreview)
        }

        totalPriceText = findViewById(R.id.totalPriceText)
        nextStepButton = findViewById(R.id.nextStepButton)
        toppingsRecyclerView = findViewById(R.id.toppingsRecyclerView)
    }

    private fun setupClickListeners() {
        // Special Offers link
        findViewById<android.view.View>(R.id.specialOffersLink)?.setOnClickListener {
            // TODO: Navigate to special offers
            android.util.Log.d("CustomisePizza", "Special offers clicked")
        }

        // Next Step button
        nextStepButton.setOnClickListener {
            // TODO: Navigate to next step (checkout)
            android.util.Log.d("CustomisePizza", "Next step clicked with ${selectedToppings.size} toppings")
            // For now, just show a toast
            android.widget.Toast.makeText(
                this,
                "Selected ${selectedToppings.size} toppings. Total: ${calculateTotalPrice()}",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            // TODO: Navigate to checkout activity when created
            // val intent = android.content.Intent(this, CheckoutActivity::class.java)
            // intent.putStringArrayListExtra("selected_toppings", ArrayList(selectedToppings))
            // intent.putExtra("total_price", calculateTotalPrice())
            // startActivity(intent)
        }
    }

    private fun setupToppingsRecyclerView() {
        val toppings = listOf(
            "Cheese",
            "Green Peppers",
            "Mushrooms",
            "Onions",
            "Olives",
            "Tomatoes",
            "Jalapeños",
            "Pineapple"
        )

        val adapter = ToppingAdapter(toppings) { topping, isSelected ->
            if (isSelected) {
                if (selectedToppings.size < maxFreeToppings) {
                    selectedToppings.add(topping)
                } else {
                    // Show message that max free toppings reached
                    android.widget.Toast.makeText(
                        this,
                        "You can only select up to $maxFreeToppings free toppings",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    return@ToppingAdapter false
                }
            } else {
                selectedToppings.remove(topping)
            }
            updateTotalPrice()
            true
        }

        toppingsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        toppingsRecyclerView.adapter = adapter
    }

    private fun updateTotalPrice() {
        val total = calculateTotalPrice()
        totalPriceText.text = String.format("%.0f", total)
    }

    private fun calculateTotalPrice(): Double {
        // Base price, toppings are free up to maxFreeToppings
        return basePrice
    }
}

class ToppingAdapter(
    private val toppings: List<String>,
    private val onToppingSelected: (String, Boolean) -> Boolean
) : RecyclerView.Adapter<ToppingAdapter.ToppingViewHolder>() {

    private val selectedItems = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ToppingViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_topping, parent, false)
        return ToppingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToppingViewHolder, position: Int) {
        val topping = toppings[position]
        holder.bind(topping, selectedItems.contains(topping))
    }

    override fun getItemCount() = toppings.size

    inner class ToppingViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.toppingCheckBox)
        private val nameText: TextView = itemView.findViewById(R.id.toppingName)

        fun bind(topping: String, isSelected: Boolean) {
            nameText.text = topping
            checkBox.isChecked = isSelected

            itemView.setOnClickListener {
                val newSelected = !checkBox.isChecked
                if (onToppingSelected(topping, newSelected)) {
                    if (newSelected) {
                        selectedItems.add(topping)
                    } else {
                        selectedItems.remove(topping)
                    }
                    checkBox.isChecked = newSelected
                }
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != isSelected) {
                    itemView.performClick()
                }
            }
        }
    }
}

