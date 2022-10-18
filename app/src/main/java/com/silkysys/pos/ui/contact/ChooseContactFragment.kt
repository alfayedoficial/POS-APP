package com.silkysys.pos.ui.contact

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.contact.list.ContactResponse
import com.silkysys.pos.data.model.contact.list.DataItem
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.FragmentChooseContactBinding
import com.silkysys.pos.ui.adapter.contact.ChooseCustomerAdapter
import com.silkysys.pos.util.BaseFragment
import com.silkysys.pos.util.dialog.alertError
import com.silkysys.pos.util.goTo
import com.silkysys.pos.util.handleApiError
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChooseContactFragment :
    BaseFragment<FragmentChooseContactBinding>(FragmentChooseContactBinding::inflate),
    View.OnClickListener, SearchView.OnQueryTextListener, OnContactClick {

    private lateinit var viewModel: ContactViewModel
    private var selectedContact: DataItem? = null


    override fun FragmentChooseContactBinding.initialize() {
        viewModel = ViewModelProvider(this@ChooseContactFragment)[ContactViewModel::class.java]
        viewModel.apply {
            initGetContacts(Constants.CUSTOMER)
            contactsResponse.observe(this@ChooseContactFragment) { updateContacts(it) }
        }
        btnSubmit.setOnClickListener(this@ChooseContactFragment)
        btnCancel.setOnClickListener(this@ChooseContactFragment)
        searchView.setOnQueryTextListener(this@ChooseContactFragment)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.add_contact_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.add_contact -> {
                        goTo(ChooseContactFragmentDirections.actionChooseContactFragmentToContactFragment())
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    override fun onClick(item: View) {
        if (item.id == R.id.btn_submit) {
            goTo(
                ChooseContactFragmentDirections.actionChooseContactFragmentToCartFragment()
                    .setContact(selectedContact)
            )
        } else if (item.id == R.id.btn_cancel) findNavController().navigateUp()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) viewModel.initGetContactBy(newText, Constants.CUSTOMER)
        return true
    }

    override fun onContactOptions(contact: DataItem?, operation: String) {
        selectedContact = contact
    }


    private fun updateContacts(response: Resource<ContactResponse>) {
        binding.progressBar.visibility = View.INVISIBLE
        if (response is Resource.Success) {
            binding.rvContacts.adapter =
                response.value.data?.let { ChooseCustomerAdapter(it, this) }
        } else {
            handleApiError(response as Resource.Failure)
            alertError {
                retryButton {
                    viewModel.initGetContacts()
                    binding.progressBar.visibility = View.VISIBLE
                }
            }.show()
        }
    }
}