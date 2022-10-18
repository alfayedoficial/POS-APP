package com.silkysys.pos.data.model.product

data class Category(
    val name: String? = "",
    val id: Int? = 0,
    val business_id: Int? = 0,
    val parent_id: Int? = 0,
    val created_by: Int? = 0,
    val category_type: String? = ""
)
