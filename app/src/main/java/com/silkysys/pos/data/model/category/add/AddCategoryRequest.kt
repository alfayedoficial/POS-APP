package com.silkysys.pos.data.model.category.add

data class AddCategoryRequest(
    val name: String? = "",
    val category_type: String? = "",
    val parent_id: Int? = 0,
    val description: String? = ""
)
