package com.silkysys.pos.data.repository

import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.product.add.AddProductRequest
import com.silkysys.pos.data.model.product.add.OpeningStockItem
import com.silkysys.pos.data.network.APIService
import com.silkysys.pos.data.network.SafeApiCall
import javax.inject.Inject

class AddProductRepo @Inject constructor(val apiService: APIService) : SafeApiCall {

    suspend fun getCategories() = safeApiCall {
        apiService.getCategories(Constants.PRODUCT, "default")
    }

    suspend fun getUnits() = safeApiCall {
        apiService.getUnits()
    }

    suspend fun getBusinessLocation() = safeApiCall {
        apiService.getBusinessLocations()
    }

    suspend fun addProduct(product: HashMap<String, Any>) = safeApiCall {
        val openingStock = OpeningStockItem(
            product[Constants.QUANTITY] as String?,
            product[Constants.PRICE_EXC_TAX] as Int?,
            product[Constants.LOCATION_ID] as Int?
        )
        val productItem = AddProductRequest(
            listOf(openingStock),
            product[Constants.CATEGORY] as Int?,
            Constants.TAX_TYPE,
            product[Constants.PRODUCT_NAME_AR] as String?,
            product[Constants.PRICE_INC_TAX] as Double?,
            product[Constants.PRODUCT_NAME] as String?,
            product[Constants.DESCRIPTION] as String?,
            product[Constants.UNIT] as Int?,
            product[Constants.PRICE_EXC_TAX] as Int?,
            product[Constants.SELLING_PRICE] as Int?
        )
        apiService.addProduct(productItem)
    }
}