package com.silkysys.pos.ui.contact

import com.silkysys.pos.data.model.contact.list.DataItem

interface OnContactClick {
    fun onContactOptions(contact: DataItem?, operation: String = "")
}