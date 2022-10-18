package com.silkysys.pos.ui.refund

import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.sell.get.DataItem
import com.silkysys.pos.data.model.sell.get.GetSpecificSellResponse
import com.silkysys.pos.data.model.sell.get.SellLines
import com.silkysys.pos.data.model.sell.refund.Products
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.FragmentRefundBinding
import com.silkysys.pos.ui.adapter.refund.RefundAdapter
import com.silkysys.pos.util.*
import com.silkysys.pos.util.dialog.alertError
import com.silkysys.pos.util.dialog.alertReturned
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RefundFragment : BaseFragment<FragmentRefundBinding>(FragmentRefundBinding::inflate),
    View.OnClickListener, OnItemRefund {

    private lateinit var viewModel: RefundViewModel
    private var sell: DataItem? = null
    var products: MutableList<Products> = mutableListOf()
    private lateinit var returnedProducts: Products
    private val args: RefundFragmentArgs by navArgs()

    private var sellLines = SellLines()
    private var returnedQty: Int = 0
    private var checkboxStatus = false


    override fun FragmentRefundBinding.initialize() {
        viewModel = ViewModelProvider(this@RefundFragment)[RefundViewModel::class.java]
        viewModel.specificSellResponse.observe(this@RefundFragment) { updateUi(it) }
        binding.tvTodayDate.text = formatDate(Constants.DATE_AM_PM)

        if (args.skuNumber == null) {
            // Alert dialog
            alertReturned {
                doneButton {
                    viewModel.initGetSpecificSell(getInvoiceNumber())
                    binding.progressBar.visibility = VISIBLE
                }
                cancelButton {
                    findNavController().navigateUp()
                }
                scanBarcode {
                    goTo(RefundFragmentDirections.actionRefundFragmentToScanInvoiceNumberFragment())
                }
            }.show()

        } else {
            viewModel.initGetSpecificSell(args.skuNumber.toString())
            binding.progressBar.visibility = VISIBLE
        }
        btnCancel.setOnClickListener(this@RefundFragment)
        btnReturned.setOnClickListener(this@RefundFragment)
    }


    override fun onClick(item: View) {
        val id = item.id
        if (id == R.id.btn_returned) returnSell(sellLines, returnedQty)
        else if (id == R.id.btn_cancel) {
            findNavController().navigateUp()
        }
    }


    override fun onClicked(model: SellLines, qty: Int, status: Boolean) {
        checkboxStatus = status
        sellLines = model
        returnedQty = qty
        returnedProducts = Products(sellLines.id, qty, sellLines.unit_price_inc_tax)
        // Fill the list of returned products
        products.add(returnedProducts)
    }

    private fun updateUi(response: Resource<GetSpecificSellResponse>) {
        binding.apply {
            progressBar.visibility = INVISIBLE
            constraintLayout.visibility = VISIBLE

            if (response is Resource.Success) {
                val specificSell = response.value.data
                specificSell?.apply {
                    sell = this
                    // update views
                    tvInvoiceNum.text = invoice_no
                    tvCustomer.text = contact?.name
                    tvDate.text = transaction_date
                    tvBusinessLocation.text = location?.name
                    rvReturned.adapter = sell_lines?.let { RefundAdapter(it, this@RefundFragment) }
                }

            } else {
                alertError {
                    retryButton {
                        alertReturned {
                            doneButton {
                                viewModel.initGetSpecificSell(getInvoiceNumber())
                                progressBar.visibility = VISIBLE
                            }
                            cancelButton {
                                findNavController().navigateUp()
                            }
                            scanBarcode {
                                goTo(RefundFragmentDirections.actionRefundFragmentToScanInvoiceNumberFragment())
                            }
                        }.show()
                    }
                }.show()
            }
        }
    }

    private fun returnSell(sellLines: SellLines?, returnedQty: Int) {
        if (checkboxStatus) {
            if (returnedQty > sellLines?.quantity!! || returnedQty == 0) {
                requireContext().toast(getString(R.string.invalid_input))
            } else {
                binding.apply {
                    btnReturned.visibility = INVISIBLE
                    progressBarBottom?.visibility = VISIBLE

                    val refund = HashMap<String, Any?>()
                    refund[Constants.TRANSACTION_ID] = sell?.id
                    refund[Constants.TRANSACTION_DATE] = "2022-04-20 21:48"
                    refund[Constants.INVOICE_NO] = sell?.invoice_no

                    Coroutines.background {
                        val response = viewModel.initCreateRefund(refund, products)
                        Coroutines.main {
                            btnReturned.visibility = VISIBLE
                            progressBarBottom?.visibility = INVISIBLE
                            if (response is Resource.Success) {
                                findNavController().navigateUp()
                            } else {
                                handleApiError(response as Resource.Failure)
                            }
                        }
                    }
                }
            }
        } else {
            requireContext().toast(getString(R.string.one_item))
        }
    }
}