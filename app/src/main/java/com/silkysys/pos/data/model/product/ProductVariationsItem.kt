package com.silkysys.pos.data.model.product

data class ProductVariationsItem(
    val variations: List<VariationsItem>?,
    val updated_at: String? = "",
    val product_id: Int? = 0,
    val name: String? = "",
    val created_at: String? = "",
    val id: Int? = 0,
    val is_dummy: Int? = 0
)