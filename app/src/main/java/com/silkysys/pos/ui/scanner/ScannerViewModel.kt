package com.silkysys.pos.ui.scanner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.pos.data.model.product.DataItem
import com.silkysys.pos.data.model.product.ProductsResponse
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.data.repository.ScannerRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(private val repo: ScannerRepo) : ViewModel() {

    val productBySkuResponse = MutableLiveData<Resource<ProductsResponse>>()
    val addToCartResponse = MutableLiveData<Boolean>()

    // Read overselling value from shared prefer
    fun readOverselling() = repo.retrieveOverselling()

    // Initialize get request to get product by sku number
    fun initGetProductBySku(skuNumber: String) {
        viewModelScope.launch {
            productBySkuResponse.value = repo.getProduct(skuNumber)
        }
    }

    // Initialize get request to add to cart
    fun initAddToCart(dataItem: DataItem, overselling: Int?, qty: Int) {
        viewModelScope.launch {
            addToCartResponse.value = repo.addToCart(dataItem, overselling, qty)
        }
    }
}