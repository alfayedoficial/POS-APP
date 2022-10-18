package com.silkysys.pos.ui.adapter.refund

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silkysys.pos.data.model.sell.get.SellLines
import com.silkysys.pos.databinding.ListItemReturnedBinding
import com.silkysys.pos.ui.refund.OnItemRefund

class RefundAdapter(
    val data: List<SellLines>,
    private val onItemRefund: OnItemRefund
) :
    RecyclerView.Adapter<RefundViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RefundViewHolder {
        return RefundViewHolder(
            ListItemReturnedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ), onItemRefund
        )
    }

    override fun onBindViewHolder(holder: RefundViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size
}