package com.silkysys.pos.data.model.sell.refund

data class Products(
    val sell_line_id: Int? = 0,
    val quantity: Int? = 0,
    val unit_price_inc_tax: Double? = 0.0
)