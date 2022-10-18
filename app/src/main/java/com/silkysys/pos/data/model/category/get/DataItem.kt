package com.silkysys.pos.data.model.category.get

data class DataItem(
    val category_type: String? = "",
    val description: String? = "",
    val created_at: String? = "",
    val created_by: Int? = 0,
    val updated_at: String? = "",
    val parent_id: Int? = 0,
    val name: String? = "",
    val id: Int? = 0,
    val business_id: Int? = 0,
) {
    override fun toString() = name.toString()
}