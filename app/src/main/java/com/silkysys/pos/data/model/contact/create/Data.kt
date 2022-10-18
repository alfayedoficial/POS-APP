package com.silkysys.pos.data.model.contact.create

data class Data(
    val prefix: String? = "",
    val type: String? = "",
    val contact_id: String? = "",
    val supplier_business_name: String? = "",
    val tax_number: String? = "",
    val id: Int? = 0,
    val first_name: String? = "",
    val email: String = "",
    val mobile: String = "",
    val last_name: String? = "",
    val created_by: Int? = 0,
    val name: String? = "",
    val business_id: Int? = 0
)