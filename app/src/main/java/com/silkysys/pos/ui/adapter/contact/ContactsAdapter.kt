package com.silkysys.pos.ui.adapter.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silkysys.pos.data.model.contact.list.DataItem
import com.silkysys.pos.databinding.ListItemContactBinding
import com.silkysys.pos.ui.contact.OnContactClick

class ContactsAdapter(
    val data: List<DataItem>,
    private val onContactClick: OnContactClick
) :
    RecyclerView.Adapter<ContactsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        return ContactsViewHolder(
            ListItemContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ), onContactClick
        )
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size
}