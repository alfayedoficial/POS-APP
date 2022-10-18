package com.silkysys.pos.data.model.product

data class Unit(
    val updated_at: String? = "",
    val created_at: String? = "",
    val short_name: String? = "",
    val id: Int? = 0,
    val actual_name: String? = "",
    val allow_decimal: Int? = 0,
    val business_id: Int? = 0,
    val created_by: Int? = 0
)