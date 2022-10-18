package com.silkysys.pos.data.model.sell.create.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SellResponse(
    val invoice_no: String? = "",
    val transaction_date: String? = "",
    val discount_type: String? = "",
    val discount_amount: Double? = 0.0,
    val contact: Contact?,
    val final_total: Double? = 0.0,
    val sell_lines: List<SellLines>?,
    val payment_lines: List<PaymentLines>?,
    val location: Location?
) : Parcelable