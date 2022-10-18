package com.silkysys.pos.data.model.product.add

data class AddProductRequest(
    val opening_stock: List<OpeningStockItem>?,
    val category_id: Int? = 0,
    val tax_type: String? = "",
    val name_ar: String? = "",
    val default_purchase_price_inc_tax: Double? = 0.0,
    val name: String? = "",
    val product_description: String? = "",
    val unit_id: Int? = 0,
    val default_purchase_price_exc_tax: Int? = 0,
    val default_selling_price_inc_tax: Int? = 0
)