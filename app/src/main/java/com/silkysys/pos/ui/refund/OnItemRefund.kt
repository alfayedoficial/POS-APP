package com.silkysys.pos.ui.refund

import com.silkysys.pos.data.model.sell.get.SellLines

interface OnItemRefund {
    fun onClicked(model: SellLines, qty: Int = 0, status: Boolean)
}