package com.silkysys.pos.ui.refund

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.pos.data.model.sell.get.GetSpecificSellResponse
import com.silkysys.pos.data.model.sell.refund.Products
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.data.repository.RefundRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RefundViewModel @Inject constructor(val repo: RefundRepo) : ViewModel() {

    val specificSellResponse = MutableLiveData<Resource<GetSpecificSellResponse>>()

    // initialize get request to get a specific sell
    fun initGetSpecificSell(invoiceNum: String) {
        viewModelScope.launch {
            specificSellResponse.value = repo.getSpecificSell(invoiceNum)
        }
    }

    // initialize post request to create a refund
    suspend fun initCreateRefund(refund: HashMap<String, Any?>, products: List<Products>?) =
        repo.createRefund(refund, products)
}