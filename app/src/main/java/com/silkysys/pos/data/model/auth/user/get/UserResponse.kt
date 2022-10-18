package com.silkysys.pos.data.model.auth.user.get

import android.os.Parcelable
import com.silkysys.pos.data.model.auth.user.get.Data
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(val data: Data) : Parcelable