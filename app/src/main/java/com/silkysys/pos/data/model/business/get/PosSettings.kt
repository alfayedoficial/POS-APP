package com.silkysys.pos.data.model.business.get

data class PosSettings(
    val disable_pay_checkout: Int? = 0,
    val disable_express_checkout: Int? = 0,
    val disable_discount: Int? = 0,
    val allow_overselling: Int? = 0,
    val disable_draft: Int? = 0,
    val disable_order_tax: Int? = 0,
)