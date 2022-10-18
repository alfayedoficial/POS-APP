package com.silkysys.pos.data.model.contact.list

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataItem(
    val id: Int? = 0,
    val business_id: Int? = 0,
    val type: String? = "",
    val supplier_business_name: String? = "",
    val name: String? = "",
    val prefix: String? = "",
    val first_name: String? = "",
    val middle_name: String? = "",
    val last_name: String? = "",
    val email: String? = "",
    val contact_id: String? = "",
    val contact_status: String? = "",
    val tax_number: String? = "",
    val mobile: String? = "",
    val landline: String? = "",
    val created_at: String? = "",
    val updated_at: String? = ""
) : Parcelable {
    override fun toString() = name.toString()
}

