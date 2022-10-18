package com.silkysys.pos.ui.cart

interface OnCartItem {

    fun onCartListener(unitPrice: Double, operation: String = "", qty: Int)
}