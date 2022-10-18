package com.silkysys.pos.data.model.contact.create

data class CreateContact(
    val type: String? = "",
    val supplier_business_name: String? = "",
    val tax_number: String? = "",
    val first_name: String? = "",
    val email: String? = "",
    val mobile: String? = "",
    val address_line_1: String? = ""
)