package com.silkysys.pos.data.repository

import com.silkysys.pos.data.local.CartDao
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.local.UserPreferences
import com.silkysys.pos.data.model.cart.Cart
import com.silkysys.pos.data.model.product.DataItem
import com.silkysys.pos.data.network.APIService
import com.silkysys.pos.data.network.SafeApiCall
import javax.inject.Inject

class ScannerRepo @Inject constructor(
    val apiService: APIService,
    val cartDao: CartDao,
    val userPreferences: UserPreferences
) :
    SafeApiCall {

    fun retrieveOverselling() = userPreferences.readInt(Constants.OVER_SELLING)

    // Create get request to get item via sku number
    suspend fun getProduct(skuNumber: String) = safeApiCall {
        apiService.getProductBySku(skuNumber)
    }

    @Suppress("SENSELESS_COMPARISON")
    suspend fun addToCart(dataItem: DataItem, overselling: Int?, qty: Int): Boolean {
        // Extract data we need from model to add to cart
        val variations = dataItem.product_variations?.get(0)?.variations?.get(0)
        val details = variations?.variation_location_details
        val stock = details?.getOrNull(0)?.qty_available
        val formattedStock = stock?.substring(0, stock.indexOf("."))?.toInt()

        // Check if the item is already existing update cart, else add a new item
        val item = cartDao.fetchInCart(dataItem.id)
        if (item != null) {
            // If over selling is not active
            return if (overselling != 1) {
                // if the stock is not empty in api before updating cart
                if (item.quantity?.plus(1)!! <= formattedStock!!) {
                    item.quantity = item.quantity?.plus(qty)
                    cartDao.updateCart(item)
                    true
                } else false
            } else {
                // Add to cart without checking on stock is available or not
                item.quantity = item.quantity?.plus(qty)
                cartDao.updateCart(item)
                true
            }

            // Add a new item into cart
        } else {
            return if (details?.size != 0) {
                // If overselling is not activated and stock is equal to zero or below that
                // Do not add to cart
                if (overselling != 1 && formattedStock!! <= 0) false
                else {
                    cartDao.addToCart(
                        Cart(
                            dataItem.id, dataItem.name, qty, stock,
                            variations?.sell_price_inc_tax, variations?.sell_price_inc_tax,
                            dataItem.id, variations?.id, details?.get(0)?.location_id
                        )
                    )
                    true
                }
            } else false
        }
    }
}