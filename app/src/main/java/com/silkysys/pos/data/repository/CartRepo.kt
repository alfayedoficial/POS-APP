package com.silkysys.pos.data.repository

import com.silkysys.pos.data.local.CartDao
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.local.UserPreferences
import com.silkysys.pos.data.model.cart.Cart
import com.silkysys.pos.data.model.sell.create.request.PaymentsItem
import com.silkysys.pos.data.model.sell.create.request.SellRequest
import com.silkysys.pos.data.model.sell.create.request.SellsItem
import com.silkysys.pos.data.network.APIService
import com.silkysys.pos.data.network.SafeApiCall
import javax.inject.Inject

class CartRepo @Inject constructor(
    private val cartDao: CartDao,
    private val apiService: APIService,
    private val userPreferences: UserPreferences
) : SafeApiCall {

    fun getCart() = cartDao.getCart()

    fun retrieveOverselling() = userPreferences.readInt(Constants.OVER_SELLING)

    suspend fun createSell(
        map: HashMap<String, Any?>,
        updatedCart: List<Cart>?
    ) = safeApiCall {
        val payment = PaymentsItem(
            map[Constants.SUB_TOTAL] as Double,
            map[Constants.PAYMENT_METHOD] as String
        )

        var discount = map[Constants.DISCOUNT] as Double
        if (discount == 0.00) {
            discount = 0.00
        }

        val sellsItem =
            SellsItem(
                listOf(payment),
                map[Constants.CONTACT_ID] as Int,
                updatedCart?.get(0)?.location_id,
                discount,
                map[Constants.INVOICE] as String,
                updatedCart
            )
        apiService.createSell(SellRequest(listOf(sellsItem)))
    }

    suspend fun getTotalPrice() = cartDao.getTotalPrice()

    suspend fun deleteAllCart() {
        cartDao.deleteAll()
        userPreferences.remove(Constants.CART_BADGE)
    }
}