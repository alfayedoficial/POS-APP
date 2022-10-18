package com.silkysys.pos.data.model.sell.create.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val name: String? = "",
    val first_name: String? = "",
    val last_name: String? = ""
) : Parcelable