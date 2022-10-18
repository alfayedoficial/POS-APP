package com.silkysys.pos.ui.adapter.home

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.silkysys.pos.R
import com.silkysys.pos.data.local.CartDao
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.product.DataItem
import com.silkysys.pos.databinding.ListItemHomeBinding
import com.silkysys.pos.ui.home.OnProductClick
import com.silkysys.pos.util.Coroutines

class HomeViewHolder(
    private val binding: ListItemHomeBinding,
    private val onProductClick: OnProductClick,
    val cartDao: CartDao
) :
    RecyclerView.ViewHolder(binding.root) {

    // Bind data
    fun bind(currentItem: DataItem) {
        binding.apply {
            currentItem.apply {
                tvProduct.text = name
                ivProduct.load(image_url) {
                    placeholder(R.drawable.loading_image)
                }

                // Retrieve qty from database if it's saved, else display 0
                Coroutines.background {
                    val item = cartDao.fetchInCart(this.id)
                    @Suppress("SENSELESS_COMPARISON")
                    if (item != null) {
                        tvQuantity.text = item.quantity.toString()
                    } else {
                        tvQuantity.text = this.localQty.toString()
                    }
                }

                // Prepare the price to display it
                product_variations?.get(0)?.variations?.get(0)?.apply {
                    val price =
                        sell_price_inc_tax.toString() + " " + root.context.getString(R.string.currency_sar)
                    tvPrice.text = price
                }

                // Click listener on whole item
                itemView.setOnClickListener {
                    onProductClick.onProductClicked(this, tvQuantity, Constants.ADD)
                }
                // Click listener on plus button
                addQuantity.setOnClickListener {
                    onProductClick.onProductClicked(this, tvQuantity, Constants.ADD)
                }
                // Click listener on mines button
                subQuantity.setOnClickListener {
                    onProductClick.onProductClicked(this, tvQuantity, Constants.SUB)
                }
            }
        }
    }
}