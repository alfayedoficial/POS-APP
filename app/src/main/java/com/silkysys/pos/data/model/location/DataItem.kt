package com.silkysys.pos.data.model.location

data class DataItem(
    val id: Int? = 0,
    val business_id: Int? = 0,
    val location_id: String? = "",
    val name: String? = "",
    val zip_code: String? = ""
) {
    override fun toString() = name.toString()
}