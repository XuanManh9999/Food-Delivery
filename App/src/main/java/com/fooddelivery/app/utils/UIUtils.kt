package com.fooddelivery.app.utils

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.fooddelivery.app.R
import com.google.android.material.snackbar.Snackbar

object UIUtils {
    
    /**
     * Hiển thị Snackbar thành công
     */
    fun showSuccessSnackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        val snackbar = Snackbar.make(view, message, duration)
        snackbar.setBackgroundTint(ContextCompat.getColor(view.context, R.color.success))
        snackbar.setTextColor(ContextCompat.getColor(view.context, R.color.text_white))
        snackbar.show()
    }
    
    /**
     * Hiển thị Snackbar lỗi
     */
    fun showErrorSnackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_LONG) {
        val snackbar = Snackbar.make(view, message, duration)
        snackbar.setBackgroundTint(ContextCompat.getColor(view.context, R.color.error))
        snackbar.setTextColor(ContextCompat.getColor(view.context, R.color.text_white))
        
        // Tăng kích thước text
        val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView?.textSize = 14f
        
        snackbar.show()
    }
    
    /**
     * Hiển thị Snackbar thông tin
     */
    fun showInfoSnackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        val snackbar = Snackbar.make(view, message, duration)
        snackbar.setBackgroundTint(ContextCompat.getColor(view.context, R.color.secondary))
        snackbar.setTextColor(ContextCompat.getColor(view.context, R.color.text_white))
        snackbar.show()
    }
    
    /**
     * Hiển thị Snackbar với action button
     */
    fun showSnackbarWithAction(
        view: View,
        message: String,
        actionText: String,
        actionListener: View.OnClickListener,
        duration: Int = Snackbar.LENGTH_LONG
    ) {
        val snackbar = Snackbar.make(view, message, duration)
        snackbar.setBackgroundTint(ContextCompat.getColor(view.context, R.color.secondary))
        snackbar.setTextColor(ContextCompat.getColor(view.context, R.color.text_white))
        snackbar.setAction(actionText, actionListener)
        snackbar.setActionTextColor(ContextCompat.getColor(view.context, R.color.text_white))
        snackbar.show()
    }
}

