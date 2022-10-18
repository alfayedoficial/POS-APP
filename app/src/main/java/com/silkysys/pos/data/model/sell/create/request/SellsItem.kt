package com.silkysys.pos.data.model.sell.create.request

import com.silkysys.pos.data.model.cart.Cart

data class SellsItem(
    val payments: List<PaymentsItem>?,
    val contact_id: Int? = 0,
    val location_id: Int? = 0,
    val discount_amount: Double? = 0.0,
    val status: String? = "",
    val products: List<Cart>?
)