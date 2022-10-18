package com.silkysys.pos.ui.adapter.refund

import androidx.recyclerview.widget.RecyclerView
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.sell.get.SellLines
import com.silkysys.pos.databinding.ListItemReturnedBinding
import com.silkysys.pos.ui.refund.OnItemRefund

class RefundViewHolder(
    val binding: ListItemReturnedBinding,
    private val onItemRefund: OnItemRefund
) :
    RecyclerView.ViewHolder(binding.root) {


    // Bind data
    fun bind(currentItem: SellLines) {
        binding.apply {
            cbProduct.text = currentItem.product_name
            val unitPrice =
                currentItem.unit_price_inc_tax.toString() + Constants.SPACE + root.context.getString(
                    R.string.sr
                )
            tvUnitPrice.text = unitPrice
            tvSellQty.text = currentItem.quantity.toString()
            // Format returned qty and display it
            val returnedQty = currentItem.quantity_returned

            tvQuantity.text = returnedQty?.substring(0, returnedQty.indexOf("."))


            cbProduct.setOnClickListener {
                // To avoid number format exception
                val qty = tvQuantity.text.toString().toInt()
                if (cbProduct.isChecked) {
                    onItemRefund.onClicked(currentItem, qty, true)
                }
            }


            addQuantity?.setOnClickListener {
                // Get returned qty
                var qty = tvQuantity.text.toString().toInt()
                // Check if qty returned is not greater than sold qty
                if (qty >= currentItem.quantity!!) {
                    tvQuantity.text = currentItem.quantity.toString()
                } else {
                    qty++
                    tvQuantity.text = qty.toString()
                }

                if (cbProduct.isChecked) {
                    onItemRefund.onClicked(currentItem, qty, true)
                }
            }

            subQuantity?.setOnClickListener {
                // Get returned qty
                var qty = tvQuantity.text.toString().toInt()
                // Check if qty returned is not equal to 0
                if (qty == 0) {
                    tvQuantity.text = "0"
                } else {
                    qty--
                    tvQuantity.text = qty.toString()
                }

                if (cbProduct.isChecked) {
                    onItemRefund.onClicked(currentItem, qty, true)
                }
            }
        }
    }
}