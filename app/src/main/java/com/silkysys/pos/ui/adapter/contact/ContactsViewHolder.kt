package com.silkysys.pos.ui.adapter.contact

import android.view.MenuItem
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.contact.list.DataItem
import com.silkysys.pos.databinding.ListItemContactBinding
import com.silkysys.pos.ui.contact.OnContactClick

class ContactsViewHolder(
    val binding: ListItemContactBinding,
    private val onContactClick: OnContactClick,
) :
    RecyclerView.ViewHolder(binding.root), PopupMenu.OnMenuItemClickListener {
    var contact: DataItem? = null

    // Bind data
    fun bind(currentItem: DataItem) {
        contact = currentItem
        binding.apply {
            if (currentItem.name == "Walk-In Customer") {
                tvName.text = currentItem.name
                tvId.text = currentItem.contact_id
            } else {
                tvId.text = currentItem.contact_id
                tvName.text = currentItem.first_name
                tvPhone.text = currentItem.mobile
            }

            itemView.setOnClickListener {
                PopupMenu(ivEdit.context, ivEdit).apply {
                    setOnMenuItemClickListener(this@ContactsViewHolder)
                    inflate(R.menu.contact_menu)
                    show()
                }
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit -> {
                onContactClick.onContactOptions(contact, Constants.EDIT_CONTACT)
                true
            }
            R.id.delete -> {
                onContactClick.onContactOptions(contact)
                true
            }
            else -> true
        }
    }
}