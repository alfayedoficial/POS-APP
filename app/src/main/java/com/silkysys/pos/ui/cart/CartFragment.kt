package com.silkysys.pos.ui.cart

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.silkysys.pos.R
import com.silkysys.pos.data.local.CartDao
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.cart.Cart
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.FragmentCartBinding
import com.silkysys.pos.ui.adapter.cart.CartAdapter
import com.silkysys.pos.util.*
import com.silkysys.pos.util.dialog.alertDiscount
import com.silkysys.pos.util.dialog.alertPayment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>(FragmentCartBinding::inflate), OnCartItem,
    AdapterView.OnItemSelectedListener, View.OnClickListener {

    @Inject
    lateinit var cartDao: CartDao
    private lateinit var viewModel: CartViewModel
    private var updatedSubTotal: Double? = 0.0
    private var updatedCart: List<Cart>? = null
    private var contactId: Int? = 152
    private var updatedDiscount: Double = 0.00
    private var invoiceStatus: String? = null
    private lateinit var tvCounter: TextView


    @Inject
    lateinit var cartCounter: CartCounter
    private val args: CartFragmentArgs by navArgs()


    override fun FragmentCartBinding.initialize() {
        initViews()
        setClickListener()

        args.contact.apply {
            if (this != null) {
                contactId = this.id
                tvSelectCustomer.text = this.name
            }
        }
        updateInvoiceType()
        viewModel.apply {
            readCart().observe(viewLifecycleOwner) { updateCart(it) }
        }
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
                else tvCounter.text = "0"
                // Click listener
                actionView.setOnClickListener {
                    onMenuItemSelected(menu.findItem(R.id.action_cart))
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.scan_barcode -> {
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.scannerFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    override fun onClick(item: View) {
        viewModel.apply {
            Constants.apply {
                when (item.id) {
                    R.id.btn_checkout -> payBill()
                    R.id.btn_add_discount -> applyDiscount()
                    R.id.tv_select_customer -> {
                        goTo(CartFragmentDirections.actionCartFragmentToChooseContactFragment())
                    }
                }
            }
        }
    }


    override fun onItemSelected(parent: AdapterView<*>?, item: View?, position: Int, p3: Long) {
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onCartListener(
        unitPrice: Double, operation: String, qty: Int
    ) {
        binding.apply {
            Constants.apply {
                val subTotal = tvSubtotal.text.toString().toDouble()
                when (operation) {
                    ADD -> {
                        updateTotalPrice(subTotal + unitPrice)
                        cartCounter.editCounter(ADD, tvCounter, tvCounter)
                    }
                    SUB -> {
                        updateTotalPrice(subTotal - unitPrice)
                        cartCounter.editCounter(SUB, tvCounter, tvCounter)
                    }
                    DEL -> {
                        updateTotalPrice(subTotal - unitPrice)
                        cartCounter.editCounter(DEL, tvCounter, tvCounter, qty)
                    }
                }
            }
        }
    }


    private fun initViews() {
        viewModel = ViewModelProvider(this@CartFragment)[CartViewModel::class.java]
        tvCounter = TextView(requireContext())
    }


    private fun setClickListener() {
        binding.apply {
            this@CartFragment.apply {
                tvSelectCustomer.setOnClickListener(this)
                spinnerInvoiceType.onItemSelectedListener = this
                btnAddDiscount.setOnClickListener(this)
                btnCheckout.setOnClickListener(this)
            }
        }
    }


    private fun updateInvoiceType() {
        invoiceStatus = getString(R.string.invoice_status)
        binding.spinnerInvoiceType.adapter = ArrayAdapter(
            requireContext(),
            R.layout.layout_spinner,
            resources.getStringArray(R.array.invoice_printing_type)
        )
    }


    private fun updateCart(cart: List<Cart>) {
        binding.apply {
            updatedCart = cart
            progressBar.visibility = INVISIBLE
            val overselling = viewModel.readOverselling()
            val cartAdapter = CartAdapter(cart, this@CartFragment, cartDao, overselling)
            rvCart.adapter = cartAdapter

            Coroutines.background {
                updatedSubTotal = viewModel.getTotal()
                Coroutines.main { updateTotalPrice(updatedSubTotal) }
            }
            if (cartAdapter.itemCount == 0) {
                group.visibility = INVISIBLE
                ivCartEmpty.visibility = VISIBLE
                tvCartEmpty.visibility = VISIBLE
            } else clCheckout.visibility = VISIBLE
        }
    }


    private fun updateTotalPrice(price: Double?) {
        binding.apply {
            tvSubtotal.text = price.toString()
            tvFinalTotal.text = (price?.minus(updatedDiscount)).toString()
        }
    }


    private fun payBill() {
        alertPayment {
            updateUi(binding.tvSubtotal.text.toString().toDouble())
            cancelButton()
            okButton {
                if (received != 0.0 && remaining >= 0 && cardAmount >= 0) {
                    paymentBinding.btnOk.visibility = INVISIBLE
                    paymentBinding.progressBar.visibility = VISIBLE
                    // Create hash map for required data
                    val map = HashMap<String, Any?>()
                    Constants.apply {
                        map[CONTACT_ID] = contactId
                        map[SUB_TOTAL] = binding.tvFinalTotal.text.toString().toDouble()
                        map[DISCOUNT] = updatedDiscount
                        map[INVOICE] = invoiceStatus
                        map[PAYMENT_METHOD] = paymentType
                    }

                    // Create api request from view model
                    Coroutines.background {
                        val response = viewModel.createSell(map, updatedCart)
                        Coroutines.main {
                            dialog?.dismiss()
                            if (response is Resource.Success) {
                                viewModel.deleteCart()
                                goTo(
                                    CartFragmentDirections
                                        .actionCartFragmentToInvoiceFragment(response.value[0])
                                )
                            } else handleApiError(response as Resource.Failure)
                        }
                    }
                } else requireContext().toast(getText(R.string.invalid_input).toString())
            }
        }.show()
    }


    private fun applyDiscount() {
        alertDiscount {
            calculateDiscount(updatedSubTotal)
            doneButton {
                updatedDiscount = discount
                binding.tvDiscount.text = discount.toString()
                if (finalTotal != null) binding.tvFinalTotal.text = finalTotal.toString()
                else binding.tvFinalTotal.text = updatedSubTotal.toString()
            }
            cancelButton()
        }.show()
    }
}