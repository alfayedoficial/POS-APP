package com.silkysys.pos.ui.product

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.Gravity.END
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.category.get.CategoriesResponse
import com.silkysys.pos.data.model.location.BusinessLocationResponse
import com.silkysys.pos.data.model.product.add.AddProductResponse
import com.silkysys.pos.data.model.unit.DataItem
import com.silkysys.pos.data.model.unit.UnitResponse
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.FragmentAddProductBinding
import com.silkysys.pos.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class AddProductFragment :
    BaseFragment<FragmentAddProductBinding>(FragmentAddProductBinding::inflate),
    View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var viewModel: AddProductViewModel
    private var unit: Int? = null
    private var unitsList: List<DataItem>? = null
    private var categoryId: Int? = null
    private var categoryList: List<com.silkysys.pos.data.model.category.get.DataItem>? = null
    private var locationId: Int? = null
    private var businessLocation: List<com.silkysys.pos.data.model.location.DataItem>? = null


    override fun FragmentAddProductBinding.initialize() {
        switchGravity()
        viewModel = ViewModelProvider(this@AddProductFragment)[AddProductViewModel::class.java]
        setClickListener()
        calculateTax()

        viewModel.apply {
            initGetBusinessLocation()
            initGetCategories()
            initGetUnits()
            categoriesResponse.observe(this@AddProductFragment) { updateCategories(it) }
            unitsResponse.observe(this@AddProductFragment) { updateUnits(it) }
            addProductResponse.observe(this@AddProductFragment) { isProductCreated(it) }
            businessLocationResponse.observe(this@AddProductFragment) { updateLocations(it) }
        }
    }


    override fun onClick(item: View) {
        if (item.id == R.id.btn_cancel) {
            findNavController().navigateUp()
        } else if (item.id == R.id.btn_add) {
            if (validateUserInput()) {
                addProduct()
            } else requireContext().toast(getString(R.string.required_field))
        } else {
            goTo(AddProductFragmentDirections.actionProductsFragmentToAddCategoryFragment())
        }
    }


    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        binding.apply {
            when (parent?.id) {
                R.id.spinner_unit -> {
                    // Save unit id
                    unit = unitsList?.get(position)?.id
                }
                R.id.spinner_category -> {
                    // Save category id
                    categoryId = categoryList?.get(position)?.id
                }
                else -> {
                    // Save location
                    locationId = businessLocation?.get(position)?.id
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}


    private fun setClickListener() {
        binding.apply {
            btnAddCategory.setOnClickListener(this@AddProductFragment)
            btnAdd.setOnClickListener(this@AddProductFragment)
            btnCancel.setOnClickListener(this@AddProductFragment)
            spinnerUnit.onItemSelectedListener = this@AddProductFragment
            spinnerCategory.onItemSelectedListener = this@AddProductFragment
            spinnerLocation.onItemSelectedListener = this@AddProductFragment
        }
    }


    private fun updateLocations(response: Resource<BusinessLocationResponse>) {
        if (response is Resource.Success) {
            businessLocation = response.value.data
            binding.spinnerLocation.adapter = ArrayAdapter(
                requireContext(),
                R.layout.layout_spinner,
                response.value.data as MutableList
            )
        } else handleApiError(response as Resource.Failure)
    }

    private fun updateCategories(response: Resource<CategoriesResponse>) {
        if (response is Resource.Success) {
            categoryList = response.value.data
            binding.spinnerCategory.adapter = ArrayAdapter(
                requireContext(),
                R.layout.layout_spinner,
                response.value.data as MutableList
            )
        } else handleApiError(response as Resource.Failure)
    }


    private fun updateUnits(response: Resource<UnitResponse>) {
        if (response is Resource.Success) {
            unitsList = response.value.data
            binding.spinnerUnit.adapter = ArrayAdapter(
                requireContext(),
                R.layout.layout_spinner,
                response.value.data as MutableList
            )
        } else handleApiError(response as Resource.Failure)
    }


    private fun calculateTax() {
        binding.apply {
            etPriceExcTax.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (s != null && s.isNotEmpty()) {
                        val priceExcTax = s.toString().toDouble()
                        val priceIncTax = priceExcTax * 1.15

                        priceIncTax.toString().apply {
                            val formattedPriceIncTax = this.substring(0, this.indexOf(".") + 2)
                            tvPriceIncTax.text = formattedPriceIncTax
                            tvPriceIncTax.setTextColor(Color.BLACK)
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
        }
    }


    private fun validateUserInput(): Boolean {
        binding.apply {
            requireContext().apply {
                return when {
                    etProductName.text.toString().trim().isEmpty() -> {
                        etProductName.error = displayError()
                        false
                    }
                    etProductNameAr.text.toString().trim().isEmpty() -> {
                        etProductNameAr.error = displayError()
                        false
                    }
                    etQty.text.toString().trim().isEmpty() -> {
                        etQty.error = displayError()
                        false
                    }
                    etPriceExcTax.text.toString().trim().isEmpty() -> {
                        etPriceExcTax.error = displayError()
                        false
                    }
                    etSellingPriceIncTax.text.toString().trim().isEmpty() -> {
                        etSellingPriceIncTax.error = displayError()
                        false
                    }
                    else -> true
                }
            }
        }
    }


    private fun addProduct() {
        binding.apply {
            btnAdd.visibility = INVISIBLE
            progressBar.visibility = VISIBLE

            Constants.apply {
                // Get input from user
                HashMap<String, Any>().apply {
                    this[PRODUCT_NAME] = etProductName.text.toString().trim()
                    this[PRODUCT_NAME_AR] = etProductNameAr.text.toString().trim()
                    this[LOCATION_ID] = locationId.toString().toInt()
                    this[UNIT] = unit.toString().toInt()

                    if (categoryId == 0) categoryId = null
                    else this[CATEGORY] = categoryId.toString().toInt()

                    this[QUANTITY] = etQty.text.toString().trim()
                    this[DESCRIPTION] = etDes.text.toString().trim()
                    this[PRICE_EXC_TAX] = etPriceExcTax.text.toString().toInt()
                    this[PRICE_INC_TAX] = tvPriceIncTax.text.toString().toDouble()
                    this[SELLING_PRICE] = etSellingPriceIncTax.text.toString().toInt()
                    // Create api request
                    viewModel.initAddProduct(this)
                }
            }
        }
    }


    private fun isProductCreated(response: Resource<AddProductResponse>) {
        binding.btnAdd.visibility = VISIBLE
        binding.progressBar.visibility = INVISIBLE
        if (response is Resource.Success) {
            findNavController().navigateUp()
            requireContext().toast(response.value.data?.name + getString(R.string.has_been_created))
        } else handleApiError(response as Resource.Failure)
    }


    // Switch gravity for views in Arabic / English language
    private fun switchGravity() {
        val locale = Locale.getDefault().language
        binding.apply {
            if (locale == Constants.LANGUAGE_AR) {
                etProductName.gravity = END
                etProductNameAr.gravity = END
                etQty.gravity = END
                etDes.gravity = END
                etPriceExcTax.gravity = END
                etSellingPriceIncTax.gravity = END

                etDes.keyListener = DigitsKeyListener.getInstance(getString(R.string.arabic_only))
            }
        }
    }
}