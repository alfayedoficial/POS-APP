package com.silkysys.pos.data.model.product

data class VariationLocationDetails(
    val id: Int? = 0,
    val product_id: Int? = 0,
    val qty_available: String? = "",
    val location_id: Int? = 0
)
