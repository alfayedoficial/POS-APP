package com.silkysys.pos.data.model.sell.create.request

data class PaymentsItem(
    val amount: Double? = 0.0,
    val method: String? = ""
)