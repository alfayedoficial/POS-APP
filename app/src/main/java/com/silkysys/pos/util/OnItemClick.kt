package com.silkysys.pos.util

interface OnItemClick {
    fun <T> onClicked(model: T, qty: Int = 0)
}