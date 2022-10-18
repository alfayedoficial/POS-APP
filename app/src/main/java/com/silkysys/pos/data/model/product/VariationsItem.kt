package com.silkysys.pos.data.model.product

data class VariationsItem(
    val default_purchase_price: Double? = 0.0,
    val default_sell_price: Double? = 0.0,
    val created_at: String? = "",
    val product_variation_id: Int? = 0,
    val profit_percent: Double? = 0.0,
    val sell_price_inc_tax: Double? = 0.0,
    val updated_at: String? = "",
    val product_id: Int? = 0,
    val name: String? = "",
    val id: Int? = 0,
    val sub_sku: String? = "",
    val variation_location_details: List<VariationLocationDetails>?
)