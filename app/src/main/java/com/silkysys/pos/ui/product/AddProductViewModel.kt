package com.silkysys.pos.ui.product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.pos.data.model.category.get.CategoriesResponse
import com.silkysys.pos.data.model.location.BusinessLocationResponse
import com.silkysys.pos.data.model.product.add.AddProductResponse
import com.silkysys.pos.data.model.unit.UnitResponse
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.data.repository.AddProductRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(val repo: AddProductRepo) : ViewModel() {

    val categoriesResponse = MutableLiveData<Resource<CategoriesResponse>>()
    val unitsResponse = MutableLiveData<Resource<UnitResponse>>()
    val addProductResponse = MutableLiveData<Resource<AddProductResponse>>()
    val businessLocationResponse = MutableLiveData<Resource<BusinessLocationResponse>>()

    // Create get request to get all categories
    fun initGetCategories() {
        viewModelScope.launch {
            categoriesResponse.value = repo.getCategories()
        }
    }

    // Create get request to get all units
    fun initGetUnits() {
        viewModelScope.launch {
            unitsResponse.value = repo.getUnits()
        }
    }

    // Create post request to add a new product
    fun initAddProduct(product: HashMap<String, Any>) {
        viewModelScope.launch {
            addProductResponse.value = repo.addProduct(product)
        }
    }

    // Create get request to get business location
    fun initGetBusinessLocation() {
        viewModelScope.launch {
            businessLocationResponse.value = repo.getBusinessLocation()
        }
    }
}