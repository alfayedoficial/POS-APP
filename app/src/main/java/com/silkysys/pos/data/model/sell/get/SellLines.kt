package com.silkysys.pos.data.model.sell.get

data class SellLines(
    val id: Int? = 0,
    val transaction_id: Int? = 0,
    val product_id: Int? = 0,
    val product_name: String? = "",
    val variation_id: Int? = 0,
    val quantity: Int? = 0,
    val quantity_returned: String? = "",
    val unit_price_inc_tax: Double? = 0.0
)