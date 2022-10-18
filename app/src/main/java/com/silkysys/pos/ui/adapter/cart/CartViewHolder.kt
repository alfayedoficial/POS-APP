package com.silkysys.pos.ui.adapter.cart

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.silkysys.pos.R
import com.silkysys.pos.data.local.CartDao
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.cart.Cart
import com.silkysys.pos.databinding.ListItemCartBinding
import com.silkysys.pos.ui.cart.OnCartItem
import com.silkysys.pos.util.Coroutines
import com.silkysys.pos.util.formatDouble
import com.silkysys.pos.util.toast

class CartViewHolder(
    val binding: ListItemCartBinding,
    private val onCartItem: OnCartItem,
    val cartDao: CartDao,
    private val overselling: Int
) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    var cart: Cart? = null

    private var totalItemPrice: Double = 0.0
    private var billTotal: Double? = 0.0
    private var qty = 0

    init {
        binding.apply {
            this@CartViewHolder.apply {
                addQuantity.setOnClickListener(this)
                subQuantity.setOnClickListener(this)
                ivDelete.setOnClickListener(this)
            }
        }
    }

    // Bind data
    fun bind(currentItem: Cart) {
        cart = currentItem
        binding.apply {
            currentItem.apply {
                tvProduct.text = product_title
                tvQuantity.text = quantity.toString()
                // Format unit price and display it
                val unitPrice =
                    formatDouble(
                        unit_price,
                        "#0.0"
                    ) + Constants.SPACE + root.context.getString(R.string.sr)
                tvUnitPrice.text = unitPrice
                // Format total price per item and display it
                tvTotal.text = formatDouble(total, "#0.0")

                // Update subtotal for all products in cart
                qty = tvQuantity.text.toString().toInt()
                if (unit_price != null) {
                    totalItemPrice = unit_price * qty
                    tvTotal.text = totalItemPrice.toString()
                    Coroutines.background {
                        billTotal = cartDao.getTotalPrice()
                    }
                }
            }
        }
    }

    override fun onClick(item: View) {
        binding.apply {
            when (item.id) {
                R.id.iv_delete -> {
                    Coroutines.background {
                        cartDao.deleteItem(cart)
                    }
                    val qty = tvQuantity.text.toString().toInt()
                    val unitPrice = cart?.unit_price
                    if (unitPrice != null) {
                        onCartItem.onCartListener(qty * unitPrice, Constants.DEL, qty)
                    }
                }

                R.id.add_quantity -> {
                    root.context.apply {
                        var qty = tvQuantity.text.toString().toInt()
                        if (overselling != 1) {
                            // Check if stock is empty or not
                            val stock = cart?.stock
                            val formattedStock = stock?.substring(0, stock.indexOf("."))?.toInt()
                            if (formattedStock != null && qty >= formattedStock) {
                                toast(getString(R.string.stock_empty))
                            } else {
                                qty++
                                editQuantity(qty, Constants.ADD)
                            }
                        } else {
                            qty++
                            editQuantity(qty, Constants.ADD)
                        }
                    }
                }

                R.id.sub_quantity -> {
                    var qty = tvQuantity.text.toString().toInt()
                    if (qty == 1) subQuantity.isClickable = false
                    else {
                        qty--
                        editQuantity(qty, Constants.SUB)
                    }
                }
            }
        }
    }

    private fun editQuantity(qty: Int, operation: String) {
        binding.apply {
            tvQuantity.text = qty.toString()
            val totalPrice = cart?.unit_price?.times(qty)
            tvTotal.text = totalPrice.toString()

            cart?.quantity = tvQuantity.text.toString().toInt()
            // Update subtotal from database
            Coroutines.background {
                cartDao.updateCart(cart)
                billTotal = cartDao.getTotalPrice()
            }
            // Display total price on view
            cart?.unit_price?.let { onCartItem.onCartListener(it, operation, qty) }
        }
    }
}