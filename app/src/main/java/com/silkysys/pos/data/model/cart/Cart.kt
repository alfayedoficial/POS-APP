package com.silkysys.pos.data.model.cart

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class Cart(
    @PrimaryKey
    val id: Int? = 0,
    val product_title: String? = "",
    var quantity: Int? = 0,
    var stock: String? = "",
    val unit_price: Double? = 0.0,
    var total: Double? = 0.0,
    val product_id: Int? = 0,
    val variation_id: Int? = 0,
    val location_id: Int? = 0,
)