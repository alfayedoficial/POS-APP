package com.silkysys.pos.ui.adapter.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silkysys.pos.data.model.contact.list.DataItem
import com.silkysys.pos.databinding.ListItemChooseContactBinding
import com.silkysys.pos.ui.contact.OnContactClick

class ChooseCustomerAdapter(
    val data: List<DataItem>,
    val onContactClick: OnContactClick
) :
    RecyclerView.Adapter<ChooseCustomerAdapter.ChooseCustomerViewHolder>() {

    private var selectedContact: String = ""
    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseCustomerViewHolder {
        return ChooseCustomerViewHolder(
            ListItemChooseContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ), onContactClick
        )
    }

    override fun onBindViewHolder(holder: ChooseCustomerViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    inner class ChooseCustomerViewHolder(
        val binding: ListItemChooseContactBinding,
        val onContactClick: OnContactClick
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private var dateItem: DataItem? = null

        // Bind data
        fun bind(currentItem: DataItem) {
            binding.apply {
                dateItem = currentItem
                if (currentItem.name == "Walk-In Customer") {
                    tvName.text = currentItem.name
                    tvId.text = currentItem.contact_id
                } else {
                    tvId.text = currentItem.contact_id
                    tvName.text = currentItem.first_name
                    tvPhone.text = currentItem.mobile
                }
                // Style item if it's selected
                rdSelectContact.isChecked = selectedContact == currentItem.contact_id
            }

            itemView.setOnClickListener { onItemClicked() }
            binding.rdSelectContact.setOnClickListener { onItemClicked() }
        }

        private fun onItemClicked() {
            if (selectedPosition != -1) notifyDataSetChanged()
            selectedContact = dateItem?.contact_id.toString()
            selectedPosition = layoutPosition
            notifyDataSetChanged()
            onContactClick.onContactOptions(dateItem)
        }
    }
}