package com.silkysys.pos.ui.contact

import android.text.method.DigitsKeyListener.getInstance
import android.view.Gravity.END
import android.view.Gravity.START
import android.view.View
import android.view.View.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.contact.list.DataItem
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.FragmentUpdateContactBinding
import com.silkysys.pos.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class UpdateContactFragment :
    BaseFragment<FragmentUpdateContactBinding>(FragmentUpdateContactBinding::inflate),
    AdapterView.OnItemSelectedListener, OnClickListener {

    private lateinit var viewModel: ContactViewModel
    private val args: UpdateContactFragmentArgs by navArgs()
    private var contactType: String? = null


    override fun FragmentUpdateContactBinding.initialize() {
        setupViewsInRtl()
        viewModel = ViewModelProvider(this@UpdateContactFragment)[ContactViewModel::class.java]
        // Update spinner contact
        spinnerContact.adapter = ArrayAdapter(
            requireContext(),
            R.layout.layout_spinner,
            resources.getStringArray(R.array.contact_type)
        )
        updateUi(args.contact)
        spinnerContact.onItemSelectedListener = this@UpdateContactFragment
        btnUpdate.setOnClickListener(this@UpdateContactFragment)
        btnCancel.setOnClickListener(this@UpdateContactFragment)
    }

    override fun onClick(item: View) {
        if (item.id == R.id.btn_update) {
            validateUserInput()
        } else {
            findNavController().navigateUp()
        }
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

    private fun updateUi(contact: DataItem) {
        binding.apply {
            etFirstName.text = setText(contact.first_name)
            etPhoneNumber.text = setText(contact.mobile)
            etEmail.text = setText(contact.email)

            if (contact.type == Constants.SUPPLIER) {
                group.visibility = VISIBLE
                spinnerContact.setSelection(1)
                etSupplierName.text = setText(contact.supplier_business_name)
                etTaxNumber.text = setText(contact.tax_number)
            }
        }
    }


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
                        else -> updateContact(contactType)
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
                        else -> updateContact(contactType)
                    }
                }
            }
        }
    }


    private fun updateContact(contactType: String?) {
        binding.apply {
            progressBar.visibility = VISIBLE
            btnUpdate.visibility = INVISIBLE

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
                    val response = args.contact.id?.let { viewModel.initUpdateContact(contact, it) }
                    Coroutines.main {
                        if (response is Resource.Success) {
                            findNavController().navigateUp()
                        } else {
                            progressBar.visibility = INVISIBLE
                            btnUpdate.visibility = VISIBLE
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
                etFirstName.gravity = START
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