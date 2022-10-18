package com.silkysys.pos.data.model.auth.user.get

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Business(
    val name: String? = "",
    val logo: String? = "",
    val logo_full_url: String? = ""
) : Parcelable