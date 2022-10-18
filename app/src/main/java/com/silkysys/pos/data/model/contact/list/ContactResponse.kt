package com.silkysys.pos.data.model.contact.list

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactResponse(
    val data: List<DataItem>?
) : Parcelable