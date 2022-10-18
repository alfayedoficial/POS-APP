package com.silkysys.pos.ui.home

import android.widget.TextView
import com.silkysys.pos.data.model.product.DataItem

interface OnProductClick {
    fun onProductClicked(model: DataItem, tvQuantity: TextView, operation: String)
}