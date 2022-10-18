package com.silkysys.pos.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.silkysys.pos.data.local.CartDao
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.category.get.CategoriesResponse
import com.silkysys.pos.data.model.product.DataItem
import com.silkysys.pos.data.network.APIService
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.data.repository.HomeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: HomeRepo,
    private val apiService: APIService,
    private val cartDao: CartDao
) : ViewModel() {

    val categoriesResponse = MutableLiveData<Resource<CategoriesResponse>>()


    // Create get request to get all products
    fun getProducts(categoryId: Int? = 0) =
        Pager(PagingConfig(pageSize = Constants.PAGE_SIZE)) {
            HomeRepo(apiService, cartDao, categoryId)
        }.flow.cachedIn(viewModelScope)

    // Create get request to get all categories
    fun initGetCategories() {
        viewModelScope.launch {
            categoriesResponse.value = repo.getCategories()
        }
    }

    // Create get request to add to cart
    suspend fun initAddToCart(dataItem: DataItem, operation: String) =
        repo.addToCart(dataItem, operation)
}