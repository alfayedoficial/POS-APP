package com.silkysys.pos.ui.adapter.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silkysys.pos.data.local.CartDao
import com.silkysys.pos.data.model.cart.Cart
import com.silkysys.pos.databinding.ListItemCartBinding
import com.silkysys.pos.ui.cart.OnCartItem

class CartAdapter(
    val data: List<Cart>,
    private val onCartItem: OnCartItem,
    val cartDao: CartDao,
    private val overselling: Int
) :
    RecyclerView.Adapter<CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(
            ListItemCartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ), onCartItem, cartDao, overselling
        )
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size
}