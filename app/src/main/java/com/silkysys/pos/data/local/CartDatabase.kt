package com.silkysys.pos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.silkysys.pos.data.model.cart.Cart

@Database(
    entities = [Cart::class],
    version = 1, exportSchema = false
)
abstract class CartDatabase : RoomDatabase() {
    abstract fun getCartDao(): CartDao
}