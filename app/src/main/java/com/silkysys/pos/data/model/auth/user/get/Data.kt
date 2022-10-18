package com.silkysys.pos.data.model.auth.user.get

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "user")
data class Data(
    @PrimaryKey
    val id: Int? = 0,
    val created_at: String? = "",
    val language: String? = "",
    val is_admin: Boolean? = false,
    val address: String? = "",
    val user_type: String? = "",
    val updated_at: String? = "",
    val surname: String? = "",
    val first_name: String? = "",
    val email: String? = "",
    val last_name: String? = "",
    val business_id: Int? = 0,
    val username: String? = "",
    val status: String? = "",
    val locations: List<Locations>,
    val business: Business
) : Parcelable