package com.silkysys.pos.data.model.business.get

data class Data(
    val id: Int? = 0,
    val name: String? = "",
    val logo: String? = "",
    val tax_number_1: String? = "",
    val pos_settings: PosSettings?,
)