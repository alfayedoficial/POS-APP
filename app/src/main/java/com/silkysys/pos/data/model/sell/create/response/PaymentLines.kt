package com.silkysys.pos.data.model.sell.create.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentLines(
    val id: Int? = 0,
    val transaction_id: Int? = 0,
    val business_id: Int? = 0,
    val amount: Double? = 0.0,
    val method: String? = "",
    val paid_on: String? = "",
    val payment_for: Int? = 0,
    val payment_ref_no: String? = ""
) : Parcelable