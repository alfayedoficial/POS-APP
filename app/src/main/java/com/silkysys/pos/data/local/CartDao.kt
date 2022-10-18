package com.silkysys.pos.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.silkysys.pos.data.model.cart.Cart

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(cart: Cart?)

    @Query("SELECT * FROM cart")
    fun getCart(): LiveData<List<Cart>>

    @Delete
    suspend fun deleteItem(cart: Cart?)

    @Query("DELETE FROM cart")
    suspend fun deleteAll()

    @Update
    suspend fun updateCart(cart: Cart?)

    @Query("SELECT Sum (quantity* unit_price) FROM cart")
    suspend fun getTotalPrice(): Double?

    @Query("SELECT * FROM cart WHERE id =:id")
    suspend fun fetchInCart(id: Int?): Cart
}