package com.silkysys.pos.data.model.sell.get

data class DataItem(
    val id: Int? = 0,
    val invoice_no: String? = "",
    val transaction_date: String? = "",
    val contact: Contact?,
    val location: Location?,
    val sell_lines: List<SellLines>?
)
