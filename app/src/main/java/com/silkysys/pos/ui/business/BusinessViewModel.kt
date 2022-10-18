package com.silkysys.pos.ui.business

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.pos.data.model.business.get.BusinessResponse
import com.silkysys.pos.data.model.business.update.BusinessUpdateResponse
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.data.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessViewModel @Inject constructor(val repo: UserRepo) : ViewModel() {

    val businessResponse = MutableLiveData<Resource<BusinessResponse>>()
    val businessUpdateResponse = MutableLiveData<Resource<BusinessUpdateResponse>>()

    // Create get request to display business details info
    fun initBusinessDetails() {
        viewModelScope.launch {
            businessResponse.value = repo.getBusinessDetails()
        }
    }

    // Create patch request to update business details info
    fun initUpdateBusinessDetails(businessName: String, taxNumber: String) {
        viewModelScope.launch {
            businessUpdateResponse.value = repo.updateBusinessDetails(businessName, taxNumber)
        }
    }
}