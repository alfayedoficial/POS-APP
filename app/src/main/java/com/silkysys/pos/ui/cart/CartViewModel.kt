package com.silkysys.pos.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.pos.data.model.cart.Cart
import com.silkysys.pos.data.repository.CartRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repo: CartRepo,
    application: Application
) : AndroidViewModel(application) {


    suspend fun createSell(
        map: HashMap<String, Any?>,
        updatedCart: List<Cart>?
    ) = repo.createSell(map, updatedCart)

    suspend fun getTotal() = repo.getTotalPrice()

    fun readCart() = repo.getCart()

    fun deleteCart() {
        viewModelScope.launch {
            repo.deleteAllCart()
        }
    }

    fun readOverselling() = repo.retrieveOverselling()
}