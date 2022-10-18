package com.silkysys.pos.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.category.get.DataItem
import com.silkysys.pos.databinding.ListItemCategoryBinding
import com.silkysys.pos.util.OnItemClick

class CategoriesAdapter(
    private val categories: List<DataItem>,
    private val onItemClick: OnItemClick
) :
    RecyclerView.Adapter<CategoriesAdapter.QuickAddViewHolder>() {

    private var selectedCategory: String? = Constants.ALL_CATEGORIES
    private var selectedPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriesAdapter.QuickAddViewHolder {
        return QuickAddViewHolder(
            ListItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ), onItemClick
        )
    }

    override fun onBindViewHolder(holder: CategoriesAdapter.QuickAddViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    inner class QuickAddViewHolder(
        val binding: ListItemCategoryBinding,
        private val onItemClick: OnItemClick
    ) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind data
        fun bind(currentItem: DataItem) {
            binding.apply {
                tvItem.text = currentItem.name
                // Style item if it's selected
                if (selectedCategory.equals(currentItem.name)) {
                    tvItem.setTextColor(ContextCompat.getColor(root.context, R.color.primary_color))
                    tvItem.setBackgroundResource(R.drawable.layer_selected_item)
                } else {
                    // Style item to it's default state
                    tvItem.setTextColor(ContextCompat.getColor(root.context, R.color.blue))
                    tvItem.setBackgroundResource(0)
                }
            }

            itemView.setOnClickListener {
                if (selectedPosition != -1) notifyDataSetChanged()
                selectedCategory = currentItem.name
                selectedPosition = layoutPosition
                notifyDataSetChanged()
                onItemClick.onClicked(currentItem)
            }
        }
    }
}