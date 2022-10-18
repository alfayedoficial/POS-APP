package com.silkysys.pos.data.model.unit

data class DataItem(
    val updated_at: String? = "",
    val created_at: String? = "",
    val short_name: String? = "",
    val id: Int? = 0,
    val actual_name: String? = "",
    val allow_decimal: Int? = 0,
    val business_id: Int? = 0,
    val created_by: Int? = 0,
) {
    override fun toString() = actual_name.toString()
}