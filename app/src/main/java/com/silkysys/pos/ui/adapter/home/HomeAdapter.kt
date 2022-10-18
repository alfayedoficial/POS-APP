package com.silkysys.pos.ui.adapter.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.silkysys.pos.data.local.CartDao
import com.silkysys.pos.data.model.product.DataItem
import com.silkysys.pos.databinding.ListItemHomeBinding
import com.silkysys.pos.ui.home.OnProductClick

class HomeAdapter(
    private val onProductClick: OnProductClick,
    val cartDao: CartDao
) :
    PagingDataAdapter<DataItem, HomeViewHolder>(COMPARATOR) {

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            ListItemHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ), onProductClick,
            cartDao
        )
    }


    object COMPARATOR : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem) =
            oldItem == newItem
    }
}