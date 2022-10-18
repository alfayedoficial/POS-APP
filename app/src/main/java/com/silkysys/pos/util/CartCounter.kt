package com.silkysys.pos.util

import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.TextView
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.local.UserPreferences
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CartCounter @Inject constructor(val userPreferences: UserPreferences) {

    private var updatedCounter = 0

    // get cart counter saved in shared prefer
    fun counter() = userPreferences.readInt(Constants.CART_BADGE)

    fun editCounter(
        operation: String,
        container: TextView,
        counter: TextView,
        qty: Int = 0,
    ) {
        // Check if there's no items in cart hide the icon
        if (container.visibility == INVISIBLE) container.visibility = VISIBLE
        // Update cart counter on all operations
        if (counter.text.toString().isNotEmpty()) {
            counter.text.toString().toInt().apply {
                updatedCounter = when (operation) {
                    Constants.ADD -> this + 1
                    Constants.SUB -> this - 1
                    else -> {
                        // Delete case
                        this - qty
                    }
                }
            }
        }

        // Display cart counter then save it
        counter.text = updatedCounter.toString()
        userPreferences.write(Constants.CART_BADGE, counter.text.toString().toInt())
    }
}
