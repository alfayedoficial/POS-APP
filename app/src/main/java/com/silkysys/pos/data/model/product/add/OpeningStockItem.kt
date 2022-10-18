package com.silkysys.pos.data.model.product.add

data class OpeningStockItem(
    val qty: String? = "",
    val purchase_price: Int? = 0,
    val location_id: Int? = 0
)