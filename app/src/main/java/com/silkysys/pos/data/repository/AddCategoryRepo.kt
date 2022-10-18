package com.silkysys.pos.data.repository

import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.category.add.AddCategoryRequest
import com.silkysys.pos.data.network.APIService
import com.silkysys.pos.data.network.SafeApiCall
import javax.inject.Inject

class AddCategoryRepo @Inject constructor(val apiService: APIService) : SafeApiCall {

    suspend fun addCategory(category: String, idSubCategory: Int?, description: String) =
        safeApiCall {
            val body = AddCategoryRequest(
                category,
                Constants.PRODUCT,
                idSubCategory,
                description
            )
            apiService.addCategory(body)
        }
}