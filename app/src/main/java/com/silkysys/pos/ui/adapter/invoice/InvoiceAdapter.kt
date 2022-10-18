package com.silkysys.pos.ui.adapter.invoice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silkysys.pos.data.model.sell.create.response.SellLines
import com.silkysys.pos.databinding.ListItemInvoiceBinding

class InvoiceAdapter(private val sell_lines: List<SellLines>) :
    RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InvoiceAdapter.InvoiceViewHolder {
        return InvoiceViewHolder(
            ListItemInvoiceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: InvoiceAdapter.InvoiceViewHolder, position: Int) {
        holder.bind(sell_lines[position], position)
    }

    override fun getItemCount() = sell_lines.size

    inner class InvoiceViewHolder(val binding: ListItemInvoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currentItem: SellLines, pos: Int) {
            binding.apply {
                val qty = currentItem.quantity
                val unitPrice = currentItem.unit_price_inc_tax

                // Display views
                tvNum.text = pos.plus(1).toString()
                tvProductName.text = currentItem.product_name
                tvQuantity.text = qty.toString()
                tvUnitPrice.text = unitPrice.toString()
                if (unitPrice != null) {
                    tvTotalPrice.text = qty?.times(unitPrice).toString()
                }
            }
        }
    }
}