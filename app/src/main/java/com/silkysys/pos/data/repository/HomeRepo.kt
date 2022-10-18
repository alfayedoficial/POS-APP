@file:Suppress("SENSELESS_COMPARISON")

package com.silkysys.pos.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.silkysys.pos.data.local.CartDao
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.cart.Cart
import com.silkysys.pos.data.model.product.DataItem
import com.silkysys.pos.data.network.APIService
import com.silkysys.pos.data.network.SafeApiCall
import javax.inject.Inject

class HomeRepo @Inject constructor(
    private val apiService: APIService,
    private val cartDao: CartDao,
    private var categoryId: Int? = 0
) : SafeApiCall, PagingSource<Int, DataItem>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataItem> {
        return try {
            val position = params.key ?: Constants.STARTING_PAGE_INDEX
            val response = apiService.getProducts(categoryId, params.loadSize, position)
            val nextKey = if (response.data!!.isEmpty()) null
            else {
                // Ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / Constants.PAGE_SIZE)
            }
            LoadResult.Page(
                data = response.data,
                prevKey = if (position == Constants.STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DataItem>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }


    suspend fun getCategories() = safeApiCall {
        apiService.getCategories(Constants.PRODUCT, "default")
    }


    suspend fun addToCart(dataItem: DataItem, operation: String): Boolean {
        // Extract data we need from model to add to cart
        val variations = dataItem.product_variations?.get(0)?.variations?.get(0)
        val details = variations?.variation_location_details
        val stock = details?.getOrNull(0)?.qty_available
        val formattedStock = stock?.substring(0, stock.indexOf("."))?.toInt()

        // Check if the item is already existing update cart, else add a new item
        val item = cartDao.fetchInCart(dataItem.id)
        return if (item != null) {
            // if the stock is not empty in api before updating cart
            if (item.quantity?.plus(1)!! <= formattedStock!!) {
                // Check if operation is add or remove or delete from cart
                when (operation) {
                    Constants.ADD -> {
                        item.quantity = item.quantity?.plus(1)
                        cartDao.updateCart(item)
                        true
                    }
                    Constants.SUB -> {
                        item.quantity = item.quantity?.minus(1)
                        cartDao.updateCart(item)
                        true
                    }
                    else -> {
                        cartDao.deleteItem(item)
                        true
                    }
                }
            } else false

            // Add a new product into cart if it's not existing
        } else {
            return if (details?.size != 0) {
                // If the stock is equal to zero or below that
                // Do not add to cart
                if (formattedStock!! <= 0) false
                else {
                    cartDao.addToCart(
                        Cart(
                            dataItem.id, dataItem.name, 1, stock,
                            variations.sell_price_inc_tax, variations.sell_price_inc_tax,
                            dataItem.id, variations.id, details[0].location_id
                        )
                    )
                    true
                }
            } else false
        }
    }
}