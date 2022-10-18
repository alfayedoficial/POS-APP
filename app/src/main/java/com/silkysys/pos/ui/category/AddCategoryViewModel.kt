package com.silkysys.pos.ui.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.pos.data.model.category.add.AddCategoryResponse
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.data.repository.AddCategoryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(val repo: AddCategoryRepo) : ViewModel() {

    val addCategoryResponse = MutableLiveData<Resource<AddCategoryResponse>>()

    // Create post request to add a new category
    fun initAddCategory(category: String, idSubCategory: Int?, description: String) {
        viewModelScope.launch {
            addCategoryResponse.value = repo.addCategory(category,idSubCategory,description)
        }
    }
}