package com.silkysys.pos.ui.category

import android.text.method.DigitsKeyListener.getInstance
import android.view.Gravity.END
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.FragmentAddCategoryBinding
import com.silkysys.pos.util.BaseFragment
import com.silkysys.pos.util.displayError
import com.silkysys.pos.util.handleApiError
import com.silkysys.pos.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddCategoryFragment :
    BaseFragment<FragmentAddCategoryBinding>(FragmentAddCategoryBinding::inflate),
    View.OnClickListener {

    private lateinit var viewModel: AddCategoryViewModel
    private var idSubCategory: Int? = 0

    override fun FragmentAddCategoryBinding.initialize() {
        viewModel = ViewModelProvider(this@AddCategoryFragment)[AddCategoryViewModel::class.java]
        setupViewsInRtl()
        viewModel.addCategoryResponse.observe(this@AddCategoryFragment) { response ->
            btnAdd.visibility = VISIBLE
            progressBar.visibility = INVISIBLE
            if (response is Resource.Success) {
                requireContext().toast(response.value.message)
                findNavController().navigateUp()
            } else handleApiError(response as Resource.Failure)
        }

        btnAdd.setOnClickListener(this@AddCategoryFragment)
        btnCancel.setOnClickListener(this@AddCategoryFragment)
    }


    override fun onClick(item: View) {
        if (item.id == R.id.btn_add) {
            if (validateUserInput()) {
                addCategory()
            } else requireContext().toast(getString(R.string.required_field))
        } else {
            findNavController().navigateUp()
        }
    }

    private fun validateUserInput(): Boolean {
        binding.apply {
            return if (etCategoryName.text.toString().trim().isEmpty()) {
                etCategoryName.error = requireContext().displayError()
                false
            } else true
        }
    }

    private fun addCategory() {
        binding.apply {
            btnAdd.visibility = INVISIBLE
            progressBar.visibility = VISIBLE
            // Get user input
            val category = etCategoryName.text.toString().trim()
            val description = etCategoryDes.text.toString().trim()
            try {
                idSubCategory = etSubCategoryId.text.toString().toInt()
            } catch (ex: Exception) {
            }
            // Create the request
            viewModel.initAddCategory(category, idSubCategory, description)
        }
    }


    private fun setupViewsInRtl() {
        binding.apply {
            Locale.getDefault().language.also { locale ->
                if (locale == Constants.LANGUAGE_AR) {
                    etCategoryName.gravity = END
                    etSubCategoryId.gravity = END
                    etCategoryDes.gravity = END

                    // Restrict inputs of edittext to arabic
                    etCategoryName.keyListener = getInstance(getString(R.string.arabic_only))
                    etCategoryDes.keyListener = getInstance(getString(R.string.arabic_only))
                }
            }
        }
    }
}