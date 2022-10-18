package com.silkysys.pos.ui.contact

import android.text.method.DigitsKeyListener.getInstance
import android.view.Gravity.END
import android.view.View
import android.view.View.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.FragmentCreateContactBinding
import com.silkysys.pos.util.BaseFragment
import com.silkysys.pos.util.Coroutines
import com.silkysys.pos.util.displayError
import com.silkysys.pos.util.handleApiError
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class CreateContactFragment :
    BaseFragment<FragmentCreateContactBinding>(FragmentCreateContactBinding::inflate),
    OnClickListener, AdapterView.OnItemSelectedListener {

    private var contactType: String? = null
    lateinit var viewModel: ContactViewModel


    override fun FragmentCreateContactBinding.initialize() {
        setupViewsInRtl()
        viewModel = ViewModelProvider(this@CreateContactFragment)[ContactViewModel::class.java]
        // Update spinner contact
        spinnerContact.adapter = ArrayAdapter(
            requireContext(),
            R.layout.layout_spinner,
            resources.getStringArray(R.array.contact_type)
        )
        btnCancel.setOnClickListener(this@CreateContactFragment)
        btnCreate.setOnClickListener(this@CreateContactFragment)
        spinnerContact.onItemSelectedListener = this@CreateContactFragment
    }

    override fun onClick(item: View) {
        if (item.id == R.id.btn_cancel) {
            findNavController().navigateUp()
        } else validateUserInput()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        binding.apply {
            if (position == 0) {
                contactType = Constants.CUSTOMER
                group.visibility = GONE
            } else {
                contactType = Constants.SUPPLIER
                group.visibility = VISIBLE
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}


    private fun validateUserInput() {
        binding.apply {
            requireContext().apply {
                val phoneNumber = etPhoneNumber.text.toString().trim()
                if (contactType == Constants.CUSTOMER) {
                    when {
                        etFirstName.text.toString().trim().isEmpty() -> {
                            etFirstName.error = displayError()
                        }
                        phoneNumber.isEmpty() -> etPhoneNumber.error = displayError()
                        else -> createContact(contactType)
                    }
                } else {
                    when {
                        etSupplierName.text.toString().trim().isEmpty() -> {
                            etSupplierName.error = displayError()
                        }
                        phoneNumber.isEmpty() -> etPhoneNumber.error = displayError()
                        etTaxNumber.text.toString().trim().isEmpty() -> {
                            etTaxNumber.error = displayError()
                        }
                        else -> createContact(contactType)
                    }
                }
            }
        }
    }


    private fun createContact(contactType: String?) {
        binding.apply {
            progressBar.visibility = VISIBLE
            btnCreate.visibility = INVISIBLE

            // Get user input after validation
            val contact = HashMap<String, String>()
            Constants.apply {
                contact[CONTACT_TYPE] = contactType.toString()
                contact[FIRST_NAME] = etFirstName.text.toString().trim()
                contact[SUPPLIER_NAME] = etSupplierName.text.toString().trim()
                contact[PHONE_NUMBER] = etPhoneNumber.text.toString().trim()
                contact[ADDRESS] = etAddress.text.toString().trim()
                contact[EMAIL] = etEmail.text.toString().trim()
                contact[TAX_NUMBER] = etTaxNumber.text.toString().trim()

                // Create api request to create a new contact
                Coroutines.background {
                    val response = viewModel.initAddContact(contact)
                    Coroutines.main {
                        if (response is Resource.Success) {
                            findNavController().navigateUp()
                        } else {
                            progressBar.visibility = INVISIBLE
                            btnCreate.visibility = VISIBLE
                            handleApiError(response as Resource.Failure)
                        }
                    }
                }
            }
        }
    }


    private fun setupViewsInRtl() {
        val locale = Locale.getDefault().language
        binding.apply {
            if (locale == Constants.LANGUAGE_AR) {
                // Set gravity to end
                etFirstName.gravity = END
                etAddress.gravity = END
                etSupplierName.gravity = END
                etTaxNumber.gravity = END

                // Restrict inputs of edittext to arabic
                etFirstName.keyListener = getInstance(getString(R.string.arabic_only))
                etAddress.keyListener = getInstance(getString(R.string.arabic_only))
                etSupplierName.keyListener = getInstance(getString(R.string.arabic_only))
            }
        }
    }
}