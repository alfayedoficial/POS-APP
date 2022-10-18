package com.silkysys.pos.ui.contact

import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.contact.list.ContactResponse
import com.silkysys.pos.data.model.contact.list.DataItem
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.FragmentContactsBinding
import com.silkysys.pos.ui.adapter.contact.ContactsAdapter
import com.silkysys.pos.util.BaseFragment
import com.silkysys.pos.util.Coroutines
import com.silkysys.pos.util.dialog.alertError
import com.silkysys.pos.util.goTo
import com.silkysys.pos.util.handleApiError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactsFragment :
    BaseFragment<FragmentContactsBinding>(FragmentContactsBinding::inflate),
    SearchView.OnQueryTextListener,
    View.OnClickListener, OnContactClick {

    private lateinit var viewModel: ContactViewModel


    override fun FragmentContactsBinding.initialize() {
        viewModel = ViewModelProvider(this@ContactsFragment)[ContactViewModel::class.java]
        viewModel.apply {
            initGetContacts()
            contactsResponse.observe(this@ContactsFragment) { updateContacts(it) }
        }
        btnAdd.setOnClickListener(this@ContactsFragment)
        btnCancel.setOnClickListener(this@ContactsFragment)
        searchView.setOnQueryTextListener(this@ContactsFragment)
    }

    override fun onClick(item: View) {
        if (item.id == R.id.btn_add) {
            goTo(ContactsFragmentDirections.actionContactsFragmentToContactFragment())
        } else if (item.id == R.id.btn_cancel) findNavController().navigateUp()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) viewModel.initGetContactBy(newText)
        return true
    }

    override fun onContactOptions(contact: DataItem?, operation: String) {
        // Edit contact
        if (operation == Constants.EDIT_CONTACT && contact != null) {
            goTo(ContactsFragmentDirections.actionContactsFragmentToUpdateContactFragment(contact))
        } else {
            // Delete a contact
            binding.progressBar.visibility = VISIBLE
            Coroutines.background {
                val response = contact?.id?.let { viewModel.initDeleteContact(it) }
                Coroutines.main {
                    binding.progressBar.visibility = INVISIBLE
                    if (response is Resource.Success) {
                        binding.rvContacts.adapter =
                            response.value.data?.let { ContactsAdapter(it, this) }
                    } else {
                        handleApiError(response as Resource.Failure)
                    }
                }
            }
        }
    }

    private fun updateContacts(response: Resource<ContactResponse>) {
        binding.progressBar.visibility = INVISIBLE
        if (response is Resource.Success) {
            binding.rvContacts.adapter =
                response.value.data?.let { ContactsAdapter(it, this) }
        } else {
            handleApiError(response as Resource.Failure)
            alertError {
                retryButton {
                    viewModel.initGetContacts()
                    binding.progressBar.visibility = VISIBLE
                }
            }.show()
        }
    }
}