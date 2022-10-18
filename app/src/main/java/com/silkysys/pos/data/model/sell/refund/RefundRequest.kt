package com.silkysys.pos.data.model.sell.refund

data class RefundRequest(
    val transaction_id: Int? = 0,
    val transaction_date: String? = "",
    val invoice_no: String? = "",
    val products: List<Products>?
)