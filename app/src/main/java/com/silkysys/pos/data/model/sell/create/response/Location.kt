package com.silkysys.pos.data.model.sell.create.response

import android.os.Parcelable
import com.silkysys.pos.data.local.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val id: Int? = 0,
    val business_id: Int? = 0,
    val location_id: String? = "",
    val name: String? = "",
    val landmark: String? = "",
    val country: String? = "",
    val state: String? = "",
    val city: String? = "",
    val zip_code: String = ""
) : Parcelable {

    override fun toString(): String {
        return city + Constants.DECIMAL_POINT + state + Constants.DECIMAL_POINT + country
    }
}