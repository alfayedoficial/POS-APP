package com.silkysys.pos.data.model.auth.user.get

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Locations(
    val business_id: Int? = 0,
    val name: String? = "",
    val country: String? = "",
    val city: String? = "",
    val mobile: String? = ""
) : Parcelable


