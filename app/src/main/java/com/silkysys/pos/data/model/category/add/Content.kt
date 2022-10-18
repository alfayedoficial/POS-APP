package com.silkysys.pos.data.model.category.add

data class Content(
    val updated_at: String? = "",
    val parent_id: Int? = 0,
    val name: String? = "",
    val category_type: String? = "",
    val description: String? = "",
    val created_at: String? = "",
    val id: Int? = 0,
    val business_id: Int? = 0,
    val created_by: Int? = 0,
    val short_code: String? = ""
)