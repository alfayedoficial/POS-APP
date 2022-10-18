package com.silkysys.pos.ui.business

import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.business.get.BusinessResponse
import com.silkysys.pos.data.model.business.update.BusinessUpdateResponse
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.FragmentBusinessDetailsBinding
import com.silkysys.pos.util.*
import com.silkysys.pos.util.dialog.alertError
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class BusinessDetailsFragment :
    BaseFragment<FragmentBusinessDetailsBinding>(FragmentBusinessDetailsBinding::inflate),
    View.OnClickListener {

    lateinit var viewModel: BusinessViewModel


    override fun FragmentBusinessDetailsBinding.initialize() {
        viewModel = ViewModelProvider(this@BusinessDetailsFragment)[BusinessViewModel::class.java]
        viewModel.apply {
            initBusinessDetails()
            businessResponse.observe(this@BusinessDetailsFragment) { displayBusinessDetails(it) }
            businessUpdateResponse.observe(this@BusinessDetailsFragment) { updateBusinessDetails(it) }
        }
        setupViewsInRtl()
        btnUpdate.setOnClickListener(this@BusinessDetailsFragment)
    }


    override fun onClick(item: View) {
        if (item.id == R.id.btn_update) {
            // Validate user input at first
            binding.apply {
                requireContext().apply {
                    val businessName = etBusinessName.text.toString()
                    val taxNumber = etTaxNumber.text.toString()
                    if (businessName.isEmpty()) {
                        etBusinessName.error = displayError()
                    } else if (taxNumber.isEmpty()) {
                        etTaxNumber.error = displayError()
                    }
                    // Create update business request
                    else {
                        btnUpdate.visibility = INVISIBLE
                        progressBarUpdate.visibility = VISIBLE
                        viewModel.initUpdateBusinessDetails(businessName, taxNumber)
                    }
                }
            }
        }
    }


    private fun updateBusinessDetails(response: Resource<BusinessUpdateResponse>) {
        binding.btnUpdate.visibility = VISIBLE
        binding.progressBarUpdate.visibility = INVISIBLE
        if (response is Resource.Success) {
            requireContext().toast(response.value.msg)
        } else {
            handleApiError(response as Resource.Failure)
        }
    }


    private fun displayBusinessDetails(response: Resource<BusinessResponse>) {
        binding.apply {
            progressBar.visibility = INVISIBLE
            if (response is Resource.Success) {
                response.value.data.apply {
                    ivLogo.visibility = VISIBLE
                    ivLogo.load(this.logo)
                    etBusinessName.text = setText(this.name)
                    etTaxNumber.text = setText(this.tax_number_1)
                }
            } else {
                // Failure case
                handleApiError(response as Resource.Failure)
                tvYourLogo.visibility = VISIBLE
                ivLogo.visibility = VISIBLE
                alertError {
                    retryButton {
                        viewModel.initBusinessDetails()
                        progressBar.visibility = VISIBLE
                        ivLogo.visibility = INVISIBLE
                        tvYourLogo.visibility = INVISIBLE
                    }
                }.show()
            }
        }
    }


    private fun setupViewsInRtl() {
        binding.apply {
            Locale.getDefault().language.also { locale ->
                if (locale == Constants.LANGUAGE_AR) {
                    etBusinessName.gravity = Gravity.END
                    etTaxNumber.gravity = Gravity.END

                    // Restrict inputs of edittext to arabic
                    etBusinessName.keyListener =
                        DigitsKeyListener.getInstance(getString(R.string.arabic_only))
                } else {
                    // Restrict inputs of edittext to english
                    etBusinessName.keyListener =
                        DigitsKeyListener.getInstance(getString(R.string.english_only))
                }
            }
        }
    }
}