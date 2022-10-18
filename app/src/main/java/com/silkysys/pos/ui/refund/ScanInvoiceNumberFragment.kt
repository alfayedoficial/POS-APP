package com.silkysys.pos.ui.refund

import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.databinding.FragmentScanInvoiceNumberBinding
import com.silkysys.pos.util.BaseFragment
import com.silkysys.pos.util.Coroutines
import com.silkysys.pos.util.goTo
import com.silkysys.pos.util.toast


class ScanInvoiceNumberFragment :
    BaseFragment<FragmentScanInvoiceNumberBinding>(FragmentScanInvoiceNumberBinding::inflate) {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var codeScanner: CodeScanner


    override fun FragmentScanInvoiceNumberBinding.initialize() {
        mediaPlayer = MediaPlayer.create(context, R.raw.barcode_sound)
        setupPermissions()
        scanBarCode()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onDestroy() {
        codeScanner.releaseResources()
        mediaPlayer.release()
        super.onPause()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CAMERA
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // Create a request
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                Constants.CAMERA_REQUEST_CODE
            )
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

            decodeCallback = DecodeCallback { sku ->
                Coroutines.main {
                    mediaPlayer.start()
                    goTo(
                        ScanInvoiceNumberFragmentDirections.actionScanInvoiceNumberFragmentToRefundFragment()
                            .setSkuNumber(sku.toString())
                    )
                }
            }
            errorCallback = ErrorCallback {
                Coroutines.main { requireContext().toast(it.message) }
            }
        }
    }
}