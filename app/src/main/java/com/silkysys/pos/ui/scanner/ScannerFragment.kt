package com.silkysys.pos.ui.scanner

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity.END
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.*
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.product.ProductsResponse
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.FragmentScannerBinding
import com.silkysys.pos.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ScannerFragment : BaseFragment<FragmentScannerBinding>(FragmentScannerBinding::inflate),
    View.OnClickListener {

    @Inject
    lateinit var cartCounter: CartCounter
    private lateinit var viewModel: ScannerViewModel
    private lateinit var tvCounter: TextView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var codeScanner: CodeScanner


    override fun FragmentScannerBinding.initialize() {
        initViews()
        setupViewsInRtl()
        setupPermissions()
        scanBarCode()
        codeScanner.startPreview()

        viewModel.apply {
            addToCartResponse.observe(this@ScannerFragment) { confirmItemAddedToCart(it) }
            productBySkuResponse.observe(this@ScannerFragment) { updateProductBySku(it) }
        }
        scannerView.setOnClickListener(this@ScannerFragment)
        btnAdd.setOnClickListener(this@ScannerFragment)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.cart_menu, menu)
                val actionView = menu.findItem(R.id.action_cart).actionView
                tvCounter = actionView.findViewById(R.id.tv_cart_badge)

                // Retrieve badge cart counter from shared prefer
                if (cartCounter.counter() != 0) tvCounter.text = cartCounter.counter().toString()
                else tvCounter.visibility = INVISIBLE
                // Click listener
                actionView.setOnClickListener {
                    onMenuItemSelected(menu.findItem(R.id.action_cart))
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_cart -> {
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.cartFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    override fun onClick(item: View) {
        if (item.id == R.id.scanner_view) {
            codeScanner.startPreview()
            binding.tvProductTitle.text = Constants.CLEAR
            binding.tvSku.text = Constants.CLEAR
        } else {
            addProductByWritingSku()
        }
    }

    override fun onPause() {
        codeScanner.releaseResources()
        mediaPlayer.release()
        super.onPause()
    }


    private fun initViews() {
        viewModel = ViewModelProvider(this@ScannerFragment)[ScannerViewModel::class.java]
        tvCounter = TextView(requireContext())
        mediaPlayer = MediaPlayer.create(context, R.raw.barcode_sound)
    }

    private fun updateProductBySku(response: Resource<ProductsResponse>) {
        binding.apply {
            // Edit visibility in case of the product was added by add button
            if (progressBar2.isVisible && !btnAdd.isVisible) {
                progressBar2.visibility = INVISIBLE
                btnAdd.visibility = VISIBLE
            }

            if (response is Resource.Success) {
                val item = response.value.data?.getOrNull(0)
                // If the product is found via sku add it to cart
                if (item != null) {
                    viewModel.initAddToCart(item, viewModel.readOverselling(), 1)
                    tvProductTitle.text = item.name
                    tvProductTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.primary_color
                        )
                    )
                } else {
                    // Display an error product not found
                    tvProductTitle.text = getString(R.string.product_not_found)
                    tvProductTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red
                        )
                    )
                    progressBar.visibility = INVISIBLE
                }
            } else handleApiError(response as Resource.Failure)
        }
    }

    private fun confirmItemAddedToCart(response: Boolean) {
        binding.progressBar.visibility = INVISIBLE
        requireContext().apply {
            if (response) {
                toast(getString(R.string.item_added))
                cartCounter.editCounter(Constants.ADD, tvCounter, tvCounter)
            } else {
                // Failure case
                toast(getString(R.string.stock_empty))
            }
        }
    }

    private fun scanBarCode() {
        codeScanner = CodeScanner(requireActivity(), binding.scannerView).apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false


            decodeCallback = DecodeCallback { skuNumber ->
                Coroutines.main {

                    //-- Add this line to solve first time open crash with BarCode scanner --//
                    mediaPlayer = MediaPlayer.create(context, R.raw.barcode_sound)
                    mediaPlayer.start()

                    binding.tvSku.text = skuNumber.toString()
                    binding.progressBar.visibility = VISIBLE
                    Coroutines.background { viewModel.initGetProductBySku(skuNumber.toString()) }
                }
            }

            errorCallback = ErrorCallback {
                Coroutines.main { requireContext().toast(it.message) }
            }
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CAMERA
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // Create a request permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                Constants.CAMERA_REQUEST_CODE
            )

        }
    }


    // This's a second option to add a product to the cart via writing sku number instead of scan barcode
    private fun addProductByWritingSku() {
        binding.apply {
            if (etManualSku.text.toString().isEmpty()) {
                etManualSku.error = requireContext().displayError()
            } else {
                progressBar2.visibility = VISIBLE
                btnAdd.visibility = INVISIBLE
                val skuNumber = etManualSku.text.toString()
                binding.tvSku.text = skuNumber
                Coroutines.background { viewModel.initGetProductBySku(skuNumber) }
            }
        }
    }


    private fun setupViewsInRtl() {
        Locale.getDefault().language.also { locale ->
            if (locale == Constants.LANGUAGE_AR) {
                binding.etManualSku.gravity = END
            }
        }
    }
}
