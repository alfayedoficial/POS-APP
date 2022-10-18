package com.silkysys.pos.ui.order

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.sell.create.response.SellResponse
import com.silkysys.pos.databinding.FragmentInvoiceBinding
import com.silkysys.pos.ui.adapter.invoice.InvoiceAdapter
import com.silkysys.pos.util.BaseFragment
import com.silkysys.pos.util.formatDouble
import com.silkysys.pos.util.goTo
import com.silkysys.pos.util.toast


@SuppressLint("SetTextI18n")
class InvoiceFragment : BaseFragment<FragmentInvoiceBinding>(FragmentInvoiceBinding::inflate),
    View.OnClickListener {

    private val args: InvoiceFragmentArgs by navArgs()
    private lateinit var sellResponse: SellResponse

    override fun FragmentInvoiceBinding.initialize() {
        (activity as AppCompatActivity).supportActionBar?.hide()
        btnConfirm.setOnClickListener(this@InvoiceFragment)
        btnPrint.setOnClickListener(this@InvoiceFragment)
        updateUi(args.order)
    }

    override fun onClick(item: View) {
        if (item.id == R.id.btn_confirm) {
            findNavController().popBackStack()
            goTo(R.id.orderConfirmedFragment)
        } else {
            requireContext().toast(Constants.DEMO)
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }


    private fun updateUi(order: SellResponse) {
        binding.apply {
            order.apply {
                sellResponse = order
                tvCompanyName.text = location?.name
                val address = location?.apply { city + state + country }
                tvAddress.text = address.toString()
                tvBillNumber.text = invoice_no
                tvDate.text = transaction_date
                tvClient.text = contact?.name

                // Display the rest of views
                rvInvoice.adapter = sell_lines?.let { InvoiceAdapter(it) }

                val subtotal = discount_amount?.let {
                    payment_lines?.get(0)?.amount?.plus(it)
                }
                tvSubtotal.text = subtotal.toString() + getSR()
                tvDiscount.text = formatDouble(discount_amount, Constants.TWO_CELL) + getSR()

                val net = subtotal?.minus(discount_amount?.toInt() ?: 0)
                tvCash.text = net.toString() + getSR()
                tvTotalPayable.text = net.toString() + getSR()
                tvBillNumberBottom.text = invoice_no
            }
        }
        generateQrCode()
        generateBarcode()
    }


    private fun generateBarcode() {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(
                sellResponse.invoice_no,
                BarcodeFormat.CODE_128,
                400,
                100, null
            )
            val encoder = com.journeyapps.barcodescanner.BarcodeEncoder()
            val bitmap = encoder.createBitmap(bitMatrix)
            binding.ivBarcode.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun generateQrCode() {
        val builder = ZatcaQRCodeGeneration()
        builder.sellerName(sellResponse.location?.name)
            .taxNumber(sellResponse.invoice_no)
            .invoiceDate(sellResponse.transaction_date)
            .totalAmount(sellResponse.final_total.toString())
        val bitmap =
            BarcodeEncoder().encodeBitmap(builder.getBase64(), BarcodeFormat.QR_CODE, 300, 300)
        binding.ivQrCode.setImageBitmap(bitmap)
    }

    private fun getSR() = Constants.SPACE + getString(R.string.sr)
}