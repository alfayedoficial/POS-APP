package com.silkysys.pos.data.model.product

data class DataItem(
    val id: Int? = 0,
    val name_ar: String? = "",
    val enable_stock: Int? = 0,
    val is_inactive: Int? = 0,
    val created_at: String? = "",
    val type: String = "",
    val updated_at: String? = "",
    val alert_quantity: String? = "",
    val sku: String? = "",
    val product_description: String? = "",
    val brand: Brand,
    val image: String? = "",
    val image_url: String? = "",
    val barcode_type: String? = "",
    val product_locations: List<ProductLocationsItem>?,
    val created_by: Int? = 0,
    val unit: Unit,
    val name: String? = "",
    val product_variations: List<ProductVariationsItem>?,
    val business_id: Int? = 0,
    val category: Category?,
    val meta: Meta?,
    var localQty: Int = 0
)